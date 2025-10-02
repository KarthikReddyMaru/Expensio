package com.cashigo.expensio.batch.refresh.transactions.steps;

import com.cashigo.expensio.common.consts.TransactionRecurrence;
import com.cashigo.expensio.dto.mapper.UUIDMapper;
import com.cashigo.expensio.model.BudgetCycle;
import com.cashigo.expensio.model.RecurringTransactionDefinition;
import com.cashigo.expensio.model.SubCategory;
import com.cashigo.expensio.model.Transaction;
import com.cashigo.expensio.repository.TransactionRepository;
import com.cashigo.expensio.service.BudgetCycleService;
import jakarta.persistence.EntityManager;
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
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Configuration
@RequiredArgsConstructor
public class CreateRecurringTransactionStep {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;

    private final EntityManager entityManager;
    private final BudgetCycleService budgetCycleService;
    private final TransactionRepository transactionRepository;
    private final UUIDMapper uuidMapper;

    @Value("${batch.chunk.size}")
    private int chunkSize;
    @Value("${zone.id}")
    private String zone;

    @Bean(name = "createRecurringTransactions")
    Step createRecurringTransactions(
            @Qualifier("readDefinitionsToCreateTransactions") JdbcPagingItemReader<RecurringTransactionDefinition> itemReader
    ) {
        return new StepBuilder("CreateRecurringTransactions", jobRepository)
                .<RecurringTransactionDefinition, Transaction>chunk(chunkSize, platformTransactionManager)
                .reader(itemReader)
                .processor(createTransactionProcessor())
                .writer(saveTransactionsWriter())
                .build();
    }

    @SneakyThrows
    @Bean("readDefinitionsToCreateTransactions") @StepScope
    JdbcPagingItemReader<RecurringTransactionDefinition> readDefinitionsToCreateTransactions (
            @Value("#{jobParameters['nextOccurrence']}") LocalDate nextOccurrence,
            @Qualifier("definitionQueryProvider") SqlPagingQueryProviderFactoryBean provider
            ) {
        JdbcPagingItemReader<RecurringTransactionDefinition> itemReader = new JdbcPagingItemReader<>();
        itemReader.setDataSource(dataSource);
        itemReader.setFetchSize(chunkSize);
        itemReader.setQueryProvider(provider.getObject());
        itemReader.setParameterValues(Map.of(
                "nextOccurrence", nextOccurrence
        ));
        itemReader.setRowMapper(recurringTransactionDefinitionRowMapper());
        return itemReader;
    }

    @Bean @StepScope
    ItemProcessor<RecurringTransactionDefinition, Transaction> createTransactionProcessor() {
        return definition -> {
            Transaction transaction = new Transaction();
            transaction.setTransactionDefinition(
                    entityManager.getReference(RecurringTransactionDefinition.class, definition.getId())
            );
            transaction.setNote(definition.getNote());
            transaction.setUserId(definition.getUserId());
            transaction.setAmount(definition.getAmount());
            transaction.setSubCategory(definition.getSubCategory());
            setTransactionDateTime(transaction, definition);
            setBudgetCycleOfTransaction(transaction);
            return transaction;
        };
    }

    @Bean @StepScope
    ItemWriter<Transaction> saveTransactionsWriter() {
        return chunk -> {
            List<? extends Transaction> transactions = chunk.getItems();
            transactionRepository.saveAll(transactions);
        };
    }

    @Bean(name = "definitionQueryProvider")
    SqlPagingQueryProviderFactoryBean definitionQueryProvider() {
        SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
        provider.setDataSource(dataSource);
        provider.setSelectClause("""
                id,
                amount,
                last_processed_instant,
                sub_category_id,
                note,
                user_id,
                transaction_recurrence_type
        """);
        provider.setFromClause("from recurring_transactions");
        provider.setWhereClause("next_occurrence = :nextOccurrence");
        provider.setSortKey("id");
        return provider;
    }

    RowMapper<RecurringTransactionDefinition> recurringTransactionDefinitionRowMapper() {
        return (rs, row) -> {
            UUID id = uuidMapper.mapToUUID(rs.getBytes("id"));
            BigDecimal amount = rs.getBigDecimal("amount");
            Timestamp lastProcessedTimeStamp = rs.getTimestamp("last_processed_instant",
                    Calendar.getInstance(TimeZone.getTimeZone("UTC")));
            Instant lastProcessedInstant = lastProcessedTimeStamp != null ? lastProcessedTimeStamp.toInstant() : null;
            SubCategory subCategory = getSubCategory(rs);
            String note = rs.getString("note");
            String userId = rs.getString("user_id");
            String recurrenceType = rs.getString("transaction_recurrence_type");
            return mapRecurringTransactionDefinition(id, amount, userId, subCategory, recurrenceType, lastProcessedInstant, note);
        };
    }

    private RecurringTransactionDefinition mapRecurringTransactionDefinition(
            UUID id, BigDecimal amount, String userId, SubCategory subCategory,
            String recurrenceType, Instant lastProcessedInstant, String note) {
        RecurringTransactionDefinition definition = new RecurringTransactionDefinition();
        definition.setId(id);
        definition.setAmount(amount);
        definition.setUserId(userId);
        definition.setSubCategory(subCategory);
        definition.setTransactionRecurrenceType(TransactionRecurrence.valueOf(recurrenceType));
        definition.setLastProcessedInstant(lastProcessedInstant);
        definition.setNote(note);
        return definition;
    }

    private SubCategory getSubCategory(ResultSet rs) throws SQLException {
        Long subCategoryId = rs.getLong("sub_category_id");
        SubCategory subCategory = new SubCategory();
        subCategory.setId(subCategoryId);
        return subCategory;
    }

    private void setBudgetCycleOfTransaction(Transaction transaction) {
        Instant transactionTime = transaction.getTransactionDateTime();
        Long subCategoryId = transaction.getSubCategory().getId();
        String userId = transaction.getUserId();
        BudgetCycle budgetCycle = budgetCycleService.getBudgetCycleByInstant(subCategoryId, transactionTime, userId);
        transaction.setBudgetCycle(budgetCycle);
    }

    private void setTransactionDateTime(Transaction transaction, RecurringTransactionDefinition definition) {
        ZoneId zoneId = ZoneId.of(zone);
        Instant lastProcessedInstant = definition.getLastProcessedInstant();
        Instant transactionInstant = null;
        TransactionRecurrence recurrence = definition.getTransactionRecurrenceType();
        if (recurrence.equals(TransactionRecurrence.WEEKLY))
            transactionInstant = lastProcessedInstant.atZone(zoneId).plusWeeks(1).toInstant();
        else if (recurrence.equals(TransactionRecurrence.DAILY)) {
            transactionInstant = lastProcessedInstant.atZone(zoneId).plusDays(1).toInstant();
        }
        transaction.setTransactionDateTime(transactionInstant);
    }
}
