package com.cashigo.expensio.batch.refresh.budget;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BudgetRefreshScheduler {

    private final JobLauncher jobLauncher;
    private final Job refreshWeeklyBudgets;

    public BudgetRefreshScheduler(JobLauncher jobLauncher, @Qualifier("refreshWeeklyBudgets") Job refreshWeeklyBudgets) {
        this.jobLauncher = jobLauncher;
        this.refreshWeeklyBudgets = refreshWeeklyBudgets;
    }


    @SneakyThrows
    @Scheduled(cron = "0 0 1 * * 1")
    public void launchWeeklyBudgetCycleRefresh() {
        log.info("Weekly Scheduler...");
        JobParameters jobParameters = new JobParametersBuilder()
                .addJobParameter("recurrenceType", new JobParameter<>("WEEKLY", String.class))
                .toJobParameters();
        jobLauncher.run(refreshWeeklyBudgets, jobParameters);
    }

}
