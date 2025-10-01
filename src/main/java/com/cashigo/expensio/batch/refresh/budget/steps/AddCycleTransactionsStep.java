package com.cashigo.expensio.batch.refresh.budget.steps;

import com.cashigo.expensio.dto.mapper.UUIDMapper;
import com.cashigo.expensio.model.BudgetCycle;
import com.cashigo.expensio.model.BudgetDefinition;
import com.cashigo.expensio.model.Category;
import com.cashigo.expensio.model.Transaction;
import com.cashigo.expensio.repository.TransactionRepository;
import com.cashigo.expensio.service.BudgetTrackingService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AddCycleTransactionsStep {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;

    private final UUIDMapper uuidMapper;
    private final BudgetTrackingService budgetTrackingService;
    private final TransactionRepository transactionRepository;

    @Value("${batch.chunk.size}")
    private int chunkSize;

    @Bean(name = "addPreviousTransactions")
    Step addPreviousTransactions(
            @Qualifier("readRefreshedBudgetDefinition") JdbcPagingItemReader<BudgetCycle> itemReader
    ) {
        return new StepBuilder("AddCurrentCycleTransactions", jobRepository)
                .<BudgetCycle, List<Transaction>>chunk(chunkSize, platformTransactionManager)
                .reader(itemReader)
                .processor(getTransactionsOfCurrentCycle())
                .writer(saveTransactionsWithCycle())
                .build();
    }

    @SneakyThrows
    @Bean(name = "readRefreshedBudgetDefinition") @StepScope
    JdbcPagingItemReader<BudgetCycle> readRefreshedBudgetDefinition(
            @Value("#{jobParameters['recurrenceType']}") String recurrenceType,
            @Qualifier("activeCyclePagingQuery") SqlPagingQueryProviderFactoryBean provider
    ) {
        JdbcPagingItemReader<BudgetCycle> itemReader = new JdbcPagingItemReader<>();
        itemReader.setDataSource(dataSource);
        itemReader.setFetchSize(chunkSize);
        itemReader.setQueryProvider(provider.getObject());
        itemReader.setParameterValues(Map.of("recurrenceType", recurrenceType));
        itemReader.setRowMapper(budgetCycleRowMapper());
        return itemReader;
    }

    @Bean
    ItemProcessor<BudgetCycle, List<Transaction>> getTransactionsOfCurrentCycle() {
        return budgetTrackingService::getTransactionsOfCurrentBudgetCycle;
    }

    @Bean
    ItemWriter<List<Transaction>> saveTransactionsWithCycle() {
        return transactions -> {
            transactions
                    .getItems()
                    .forEach(transactionRepository::saveAll);
        };
    }

    @Bean
    SqlPagingQueryProviderFactoryBean activeCyclePagingQuery() {
        SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
        provider.setDataSource(dataSource);
        provider.setSelectClause("""
                bc.id as budget_cycle_id,
                bd.id as budget_def_id,
               	bd.user_id as user_id,
               	bd.category_id as category_id,
               	bc.cycle_start_date_time as start_date_time,
               	bc.cycle_end_date_time as end_date_time
        """);
        provider.setFromClause("""
                from budget_definition bd join budget_cycle bc
                on bd.id = bc.budget_definition_id and bc.is_active = 1
        """);
        provider.setWhereClause("bd.budget_recurrence_type = :recurrenceType");
        provider.setSortKey("budget_cycle_id");
        return provider;
    }

    @Bean
    RowMapper<BudgetCycle> budgetCycleRowMapper() {
        return (rs, row) -> {
            BudgetDefinition budgetDefinition = getBudgetDefinition(rs);
            return getBudgetCycle(rs, budgetDefinition);
        };
    }

    private BudgetCycle getBudgetCycle(ResultSet rs, BudgetDefinition budgetDefinition) throws SQLException {
        UUID budgetCycleId = uuidMapper.map(rs.getBytes("budget_cycle_id"));
        Timestamp start_time = rs.getTimestamp("start_date_time", Calendar.getInstance(TimeZone.getTimeZone("UTC")));
        Timestamp end_time = rs.getTimestamp("end_date_time", Calendar.getInstance(TimeZone.getTimeZone("UTC")));
        Instant start = start_time != null ? start_time.toInstant() : null;
        Instant end = end_time != null ? end_time.toInstant() : null;
        BudgetCycle budgetCycle = new BudgetCycle();
        budgetCycle.setId(budgetCycleId);
        budgetCycle.setBudgetDefinition(budgetDefinition);
        budgetCycle.setCycleStartDateTime(start);
        budgetCycle.setCycleEndDateTime(end);
        return budgetCycle;
    }

    private BudgetDefinition getBudgetDefinition(ResultSet rs) throws SQLException {
        Category category = getCategory(rs);
        UUID budgetDefinitionId = uuidMapper.map(rs.getBytes("budget_def_id"));
        String userId = rs.getString("user_id");
        BudgetDefinition budgetDefinition = new BudgetDefinition();
        budgetDefinition.setId(budgetDefinitionId);
        budgetDefinition.setUserId(userId);
        budgetDefinition.setCategory(category);
        return budgetDefinition;
    }

    private Category getCategory(ResultSet rs) throws SQLException {
        Long categoryId = rs.getLong("category_id");
        Category category = new Category();
        category.setId(categoryId);
        return category;
    }
}
