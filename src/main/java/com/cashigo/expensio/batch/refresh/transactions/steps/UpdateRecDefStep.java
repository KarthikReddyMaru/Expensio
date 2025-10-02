package com.cashigo.expensio.batch.refresh.transactions.steps;

import com.cashigo.expensio.batch.refresh.transactions.model.RecurrencingTranDefRecord;
import com.cashigo.expensio.common.consts.TransactionRecurrence;
import com.cashigo.expensio.dto.mapper.UUIDMapper;
import com.cashigo.expensio.repository.RecurringTransactionDefinitionRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class UpdateRecDefStep {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;

    private final UUIDMapper uuidMapper;

    @Value("${batch.chunk.size}")
    private int chunkSize;
    @Value("${zone.id}")
    private String zone;

    @Bean(name = "updateRecurrenceDefinition")
    Step updateRecurrenceDefinition(
            @Qualifier("readDefinitionsToUpdateDef") JdbcPagingItemReader<RecurrencingTranDefRecord> itemReader,
            @Qualifier("saveRecurTranDefinitions") JdbcBatchItemWriter<RecurrencingTranDefRecord> saveRecurTranDefinitions
    ) {
        return new StepBuilder("UpdateRecurrenceDefinition", jobRepository)
                .<RecurrencingTranDefRecord, RecurrencingTranDefRecord>chunk(chunkSize, platformTransactionManager)
                .reader(itemReader)
                .processor(updateRecurTranDefinitionProcessor())
                .writer(saveRecurTranDefinitions)
                .build();
    }

    @SneakyThrows
    @Bean(name = "readDefinitionsToUpdateDef") @StepScope
    JdbcPagingItemReader<RecurrencingTranDefRecord> readDefinitionsToUpdateDef(
            @Value("#{jobParameters['nextOccurrence']}") LocalDate nextOccurrence,
            @Qualifier("definitionQueryProviderToUpdate") SqlPagingQueryProviderFactoryBean provider
    ) {
        JdbcPagingItemReader<RecurrencingTranDefRecord> itemReader = new JdbcPagingItemReader<>();
        itemReader.setDataSource(dataSource);
        itemReader.setFetchSize(chunkSize);
        itemReader.setQueryProvider(provider.getObject());
        itemReader.setParameterValues(Map.of("nextOccurrence", nextOccurrence));
        itemReader.setRowMapper(recurrencingTranDefRecordRowMapper());
        return itemReader;
    }

    @Bean @StepScope
    ItemProcessor<RecurrencingTranDefRecord, RecurrencingTranDefRecord> updateRecurTranDefinitionProcessor() {
        return this::getUpdatedRecurringDefinition;
    }

    @Bean(name = "saveRecurTranDefinitions") @StepScope
    JdbcBatchItemWriter<RecurrencingTranDefRecord> saveRecurTranDefinitions() {
        return new JdbcBatchItemWriterBuilder<RecurrencingTranDefRecord>()
                .dataSource(dataSource)
                .sql("""
                       update recurring_transactions
                       set next_occurrence = :nextOccurrence, last_processed_instant = :lastProcessedInstant
                       where id = :id
                """)
                .itemSqlParameterSourceProvider(record ->
                        new MapSqlParameterSource().addValues(
                                Map.of (
                                "id", uuidMapper.mapToBytes(record.id()),
                                "nextOccurrence", Date.valueOf(record.nextOccurrence()),
                                "lastProcessedInstant", record.lastProcessedInstant()
                                                .atZone(ZoneOffset.UTC).toLocalDateTime()
                                )))
                .build();
    }

    @Bean
    SqlPagingQueryProviderFactoryBean definitionQueryProviderToUpdate() {
        SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
        provider.setDataSource(dataSource);
        provider.setSelectClause("""
                id,
                last_processed_instant,
                next_occurrence,
                transaction_recurrence_type
        """);
        provider.setFromClause("from recurring_transactions");
        provider.setWhereClause("next_occurrence = :nextOccurrence");
        provider.setSortKey("id");
        return provider;
    }

    RowMapper<RecurrencingTranDefRecord> recurrencingTranDefRecordRowMapper() {
        return (rs, row) -> {
            UUID id = uuidMapper.mapToUUID(rs.getBytes("id"));
            Timestamp lastProcessedTimeStamp = rs.getTimestamp("last_processed_instant",
                    Calendar.getInstance(TimeZone.getTimeZone("UTC")));
            Instant lastProcessedInstant = lastProcessedTimeStamp != null ? lastProcessedTimeStamp.toInstant() : null;
            LocalDate nextOccurrence = rs.getDate("next_occurrence").toLocalDate();
            String transactionRecurrence = rs.getString("transaction_recurrence_type");
            TransactionRecurrence recurrence = TransactionRecurrence.valueOf(transactionRecurrence);
            return new RecurrencingTranDefRecord(id, lastProcessedInstant, nextOccurrence, recurrence);
        };
    }

    private RecurrencingTranDefRecord getUpdatedRecurringDefinition(RecurrencingTranDefRecord defRecord) {
        ZoneId zoneId = ZoneId.of(zone);
        TransactionRecurrence recurrence = defRecord.transactionRecurrence();
        LocalDate nextOccurrence = null;
        Instant lastProcessedDateTime = null;
        if (recurrence.equals(TransactionRecurrence.DAILY)) {
            nextOccurrence = defRecord.nextOccurrence().plusDays(1);
            lastProcessedDateTime = defRecord.lastProcessedInstant().atZone(zoneId).plusDays(1).toInstant();
        } else if (recurrence.equals(TransactionRecurrence.WEEKLY)) {
            nextOccurrence = defRecord.nextOccurrence().plusWeeks(1);
            lastProcessedDateTime = defRecord.lastProcessedInstant().atZone(zoneId).plusWeeks(1).toInstant();
        }
        return new RecurrencingTranDefRecord(defRecord.id(), lastProcessedDateTime, nextOccurrence, recurrence);
    }

}
