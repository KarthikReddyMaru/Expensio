package com.cashigo.expensio.batch.refresh.budget.steps;

import com.cashigo.expensio.common.consts.BudgetRecurrence;
import com.cashigo.expensio.dto.mapper.UUIDMapper;
import com.cashigo.expensio.model.BudgetCycle;
import com.cashigo.expensio.model.BudgetDefinition;
import com.cashigo.expensio.repository.BudgetCycleRepository;
import com.cashigo.expensio.service.BudgetCycleService;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class CreateActiveCyclesStep {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;

    private final BudgetCycleService budgetCycleService;
    private final BudgetCycleRepository budgetCycleRepository;
    private final UUIDMapper uuidMapper;

    @Value("${batch.chunk.size}")
    private int chunkSize;

    @Bean(name = "createActiveCycles")
    Step createActiveCycles(
            @Qualifier("budgetDefinitionItemReader") JdbcPagingItemReader<BudgetDefinition> budgetDefinitionItemReader) {
        return new StepBuilder("CreateActiveCycle", jobRepository)
                .<BudgetDefinition, BudgetCycle>chunk(chunkSize, platformTransactionManager)
                .reader(budgetDefinitionItemReader)
                .processor(budgetDefinitionProcessor())
                .writer(budgetCycleItemWriter())
                .build();
    }

    @SneakyThrows
    @StepScope @Bean
    JdbcPagingItemReader<BudgetDefinition> budgetDefinitionItemReader(
            @Value("#{jobParameters['recurrenceType']}") String recurrenceType,
            @Qualifier("budgetPagingQuery") SqlPagingQueryProviderFactoryBean budgetPagingQuery
    ) {

        JdbcPagingItemReader<BudgetDefinition> pagingItemReader = new JdbcPagingItemReader<>();
        pagingItemReader.setDataSource(dataSource);
        pagingItemReader.setFetchSize(chunkSize);
        pagingItemReader.setQueryProvider(budgetPagingQuery.getObject());
        pagingItemReader.setParameterValues(Map.of("recurrenceType", recurrenceType));
        pagingItemReader.setRowMapper(rowMapper(recurrenceType));

        return pagingItemReader;
    }

    @Bean @StepScope
    ItemProcessor<BudgetDefinition, BudgetCycle> budgetDefinitionProcessor() {
        return budgetCycleService::createBudgetCycle;
    }

    @Bean @StepScope
    ItemWriter<BudgetCycle> budgetCycleItemWriter() {
        return chunk -> {
            List<? extends BudgetCycle> cycles = chunk.getItems();
            budgetCycleRepository.saveAll(cycles);
        };
    }

    @SneakyThrows @Bean
    public SqlPagingQueryProviderFactoryBean budgetPagingQuery() {

        SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
        provider.setDataSource(dataSource);
        provider.setSelectClause("id");
        provider.setFromClause("from budget_definition");
        provider.setWhereClause("budget_recurrence_type = :recurrenceType");
        provider.setSortKey("id");

        return provider;

    }

    private RowMapper<BudgetDefinition> rowMapper(String recurrenceType) {
        return (rs, row) -> {
            UUID budgetDefinitionId = uuidMapper.map(rs.getBytes("id"));
            BudgetDefinition budgetDefinition = new BudgetDefinition();
            budgetDefinition.setId(budgetDefinitionId);
            budgetDefinition.setBudgetRecurrenceType(BudgetRecurrence.valueOf(recurrenceType));
            return budgetDefinition;
        };
    }

}
