package com.cashigo.expensio.batch.refresh.transactions;

import lombok.SneakyThrows;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class RecurringTransactionsScheduler {

    private final JobLauncher jobLauncher;
    private final Job processRecurringTransactions;

    public RecurringTransactionsScheduler(JobLauncher jobLauncher,
                                          @Qualifier("processRecurringTransactions") Job processRecurringTransactions) {
        this.jobLauncher = jobLauncher;
        this.processRecurringTransactions = processRecurringTransactions;
    }

    @Scheduled(cron = "0 0 1 * * 1")
    @SneakyThrows
    public void processRecurringTransactions() {
        LocalDate today = LocalDate.now();
        JobParameters jobParameters = new JobParametersBuilder()
                .addJobParameter("nextOccurrence",new JobParameter<>(today, LocalDate.class))
                .toJobParameters();
        jobLauncher.run(processRecurringTransactions, jobParameters);
    }
}
