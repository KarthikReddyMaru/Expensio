package com.cashigo.expensio.batch.launcher;

import com.cashigo.expensio.common.consts.BudgetRecurrence;
import lombok.SneakyThrows;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/batch")
public class BatchLauncher {

    private final JobLauncher jobLauncher;
    private final Job refreshWeeklyBudgets;
    private final Job refreshMonthlyBudgets;

    public BatchLauncher(JobLauncher jobLauncher,
                         @Qualifier("refreshWeeklyBudgets") Job refreshWeeklyBudgets,
                         @Qualifier("refreshMonthlyBudgets") Job refreshMonthlyBudgets) {
        this.jobLauncher = jobLauncher;
        this.refreshWeeklyBudgets = refreshWeeklyBudgets;
        this.refreshMonthlyBudgets = refreshMonthlyBudgets;
    }

    @GetMapping("/budget/week")
    @SneakyThrows
    public ResponseEntity<Void> refreshWeeklyBudgets() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addJobParameter("recurrenceType", new JobParameter<>(BudgetRecurrence.WEEKLY.name(), String.class))
                .toJobParameters();
        jobLauncher.run(refreshWeeklyBudgets, jobParameters);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/budget/month")
    @SneakyThrows
    public ResponseEntity<Void> refreshMonthlyBudgets() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addJobParameter("recurrenceType", new JobParameter<>(BudgetRecurrence.MONTHLY.name(), String.class))
                .toJobParameters();
        jobLauncher.run(refreshMonthlyBudgets, jobParameters);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
