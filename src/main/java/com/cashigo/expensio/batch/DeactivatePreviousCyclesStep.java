package com.cashigo.expensio.batch;

import com.cashigo.expensio.dto.mapper.UUIDMapper;
import com.cashigo.expensio.model.BudgetCycle;
import com.cashigo.expensio.model.BudgetDefinition;
import com.cashigo.expensio.repository.BudgetCycleRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class DeactivatePreviousCyclesStep {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;

    private final BudgetCycleRepository budgetCycleRepository;
    private final UUIDMapper uUIDMapper;

    @Value("${batch.chunk.size}")
    private int chunkSize;

    @Bean(name = "deactivatePreviousCycles")
    Step deactivatePreviousCycles(
            @Qualifier("readBudgetDefinitions") JdbcPagingItemReader<BudgetRefreshRecord> itemReader
    ) {
        return new StepBuilder("DeactivatePreviousCycles", jobRepository)
                .<BudgetRefreshRecord, BudgetCycle>chunk(chunkSize, platformTransactionManager)
                .reader(itemReader)
                .processor(budgetRefreshProcessor())
                .writer(deactivatePreviousCyclesWriter())
                .build();
    }

    @SneakyThrows
    @Bean @StepScope
    JdbcPagingItemReader<BudgetRefreshRecord> readBudgetDefinitions(
            @Value("#{jobParameters['recurrenceType']}") String recurrenceType
    ) {

        JdbcPagingItemReader<BudgetRefreshRecord> itemReader = new JdbcPagingItemReader<>();
        itemReader.setDataSource(dataSource);
        itemReader.setFetchSize(chunkSize);

        SqlPagingQueryProviderFactoryBean provider = getSqlPagingQueryProviderFactoryBean();

        itemReader.setQueryProvider(provider.getObject());
        itemReader.setRowMapper(budgetDefinitionRowMapper());
        itemReader.setParameterValues(Map.of("recurrenceType", recurrenceType));

        return itemReader;
    }

    @Bean
    public SqlPagingQueryProviderFactoryBean getSqlPagingQueryProviderFactoryBean() {
        SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
        provider.setDataSource(dataSource);
        provider.setSelectClause("""
                bd.id as budget_def_id, bd.user_id as user_id,
                bc.id as budget_cycle_id,
                bc.cycle_start_date_time as start, bc.cycle_end_date_time as end
        """);
        provider.setFromClause("""
                from budget_definition bd
                join budget_cycle bc on bd.id = bc.budget_definition_id and bc.is_active = 1
        """);
        provider.setWhereClause("budget_recurrence_type = :recurrenceType");
        provider.setSortKey("budget_def_id");
        return provider;
    }

    RowMapper<BudgetRefreshRecord> budgetDefinitionRowMapper() {

        return (rs, row) -> {

            UUID budgetDefinitionId = uUIDMapper.map(rs.getBytes("budget_def_id"));
            UUID budgetCycleId = uUIDMapper.map(rs.getBytes("budget_cycle_id"));

            Timestamp cycleStart = rs.getTimestamp("start");
            Instant start = cycleStart != null ? cycleStart.toInstant() : null;
            Timestamp cycleEnd = rs.getTimestamp("end");
            Instant end = cycleEnd != null ? cycleEnd.toInstant() : null;

            String userId = rs.getString("user_id");

            return new BudgetRefreshRecord(budgetDefinitionId, budgetCycleId, start, end, userId);
        };
    }

    @Bean @StepScope
    ItemProcessor<BudgetRefreshRecord, BudgetCycle> budgetRefreshProcessor() {
        return budgetRefreshRecord -> {

            BudgetDefinition budgetDefinition = new BudgetDefinition();
            budgetDefinition.setId(budgetRefreshRecord.budgetDefId());

            BudgetCycle budgetCycle = new BudgetCycle();
            budgetCycle.setId(budgetRefreshRecord.budgetCycleId());
            budgetCycle.setBudgetDefinition(budgetDefinition);
            budgetCycle.setCycleStartDateTime(budgetRefreshRecord.start());
            budgetCycle.setCycleEndDateTime(budgetRefreshRecord.end());
            budgetCycle.setActive(false);
            return budgetCycle;
        };
    }

    @Bean @StepScope
    ItemWriter<BudgetCycle> deactivatePreviousCyclesWriter() {
        return chunk -> {
            List<? extends BudgetCycle> previousActiveCycles = chunk.getItems();
            budgetCycleRepository.saveAll(previousActiveCycles);
        };
    }

}
