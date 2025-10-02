package com.cashigo.expensio.batch.refresh.transactions;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RecurringTransactionsBatch {


    private final JobRepository jobRepository;
    private final Step createRecurringTransactionStep;
    private final Step updateRecurrenceDefinition;

    public RecurringTransactionsBatch(JobRepository jobRepository,
                                      @Qualifier("createRecurringTransactions") Step createRecurringTransactionStep,
                                      @Qualifier("updateRecurrenceDefinition") Step updateRecurrenceDefinition) {
        this.jobRepository = jobRepository;
        this.createRecurringTransactionStep = createRecurringTransactionStep;
        this.updateRecurrenceDefinition = updateRecurrenceDefinition;
    }

    @Bean(name = "processRecurringTransactions")
    Job processRecurringTransactions() {
        return new JobBuilder("CreateRecurringTransactions", jobRepository)
                .start(createRecurringTransactionStep)
                .next(updateRecurrenceDefinition)
                .build();
    }

}
