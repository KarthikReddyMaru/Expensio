package com.cashigo.expensio.batch.refresh.budget;

import com.cashigo.expensio.common.consts.BudgetRecurrence;
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
    private final Job refreshMonthlyBudgets;

    public BudgetRefreshScheduler(JobLauncher jobLauncher,
                                  @Qualifier("refreshWeeklyBudgets") Job refreshWeeklyBudgets,
                                  @Qualifier("refreshMonthlyBudgets") Job refreshMonthlyBudgets) {
        this.jobLauncher = jobLauncher;
        this.refreshWeeklyBudgets = refreshWeeklyBudgets;
        this.refreshMonthlyBudgets = refreshMonthlyBudgets;
    }


    @SneakyThrows
    @Scheduled(cron = "0 0 1 * * 1")
    public void launchWeeklyBudgetCycleRefresh() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addJobParameter("recurrenceType", new JobParameter<>(BudgetRecurrence.WEEKLY.name(), String.class))
                .toJobParameters();
        jobLauncher.run(refreshWeeklyBudgets, jobParameters);
    }

    @SneakyThrows
    @Scheduled(cron = "0 20 1 * * 1")
    public void launchMonthlyBudgetCycleRefresh() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addJobParameter("recurrenceType", new JobParameter<>(BudgetRecurrence.MONTHLY.name(), String.class))
                .toJobParameters();
        jobLauncher.run(refreshMonthlyBudgets, jobParameters);
    }

}
