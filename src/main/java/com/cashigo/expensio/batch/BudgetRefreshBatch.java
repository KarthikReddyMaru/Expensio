package com.cashigo.expensio.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BudgetRefreshBatch {

    private final JobRepository jobRepository;
    private final Step deactivatePreviousCyclesStep;
    private final Step createActiveCycles;

    public BudgetRefreshBatch(JobRepository jobRepository,
                              @Qualifier("deactivatePreviousCycles") Step deactivatePreviousCyclesStep,
                              @Qualifier("createActiveCycles") Step createActiveCycles) {
        this.jobRepository = jobRepository;
        this.deactivatePreviousCyclesStep = deactivatePreviousCyclesStep;
        this.createActiveCycles = createActiveCycles;
    }

    @Bean(name = "refreshWeeklyBudgets")
    Job refreshWeeklyBudgets() {
        return new JobBuilder("RefreshWeeklyBudgets", jobRepository)
                .start(deactivatePreviousCyclesStep)
                .next(createActiveCycles)
                .build();
    }


}
