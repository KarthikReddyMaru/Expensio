package com.cashigo.expensio.batch.launcher;

import com.cashigo.expensio.common.consts.BudgetRecurrence;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/batch")
@Tag(name = "Refresh Transactions/Cycles")
public class BatchLauncher {

    private final JobLauncher jobLauncher;
    private final Job refreshWeeklyBudgets;
    private final Job refreshMonthlyBudgets;
    private final Job processRecurringTransactions;

    public BatchLauncher(JobLauncher jobLauncher,
                         @Qualifier("refreshWeeklyBudgets") Job refreshWeeklyBudgets,
                         @Qualifier("refreshMonthlyBudgets") Job refreshMonthlyBudgets,
                         @Qualifier("processRecurringTransactions") Job processRecurringTransactions) {
        this.jobLauncher = jobLauncher;
        this.refreshWeeklyBudgets = refreshWeeklyBudgets;
        this.refreshMonthlyBudgets = refreshMonthlyBudgets;
        this.processRecurringTransactions = processRecurringTransactions;
    }

    @GetMapping("/budget/week")
    @SneakyThrows
    @Operation(summary = "Refresh weekly budgets") @ApiResponse(responseCode = "201")
    public ResponseEntity<Void> refreshWeeklyBudgets() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addJobParameter("recurrenceType", new JobParameter<>(BudgetRecurrence.WEEKLY.name(), String.class))
                .toJobParameters();
        jobLauncher.run(refreshWeeklyBudgets, jobParameters);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/budget/month")
    @SneakyThrows
    @Operation(summary = "Refresh monthly budgets") @ApiResponse(responseCode = "201")
    public ResponseEntity<Void> refreshMonthlyBudgets() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addJobParameter("recurrenceType", new JobParameter<>(BudgetRecurrence.MONTHLY.name(), String.class))
                .toJobParameters();
        jobLauncher.run(refreshMonthlyBudgets, jobParameters);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/transactions/{date}")
    @Operation(
            summary = "Create due dated recurrence transactions",
            parameters = @Parameter(name = "date", example = "2025-10-10")
    )
    @ApiResponse(responseCode = "201")
    @SneakyThrows
    public ResponseEntity<Void> processRecurringTransactions(@PathVariable LocalDate date) {
        JobParameters jobParameters = new JobParametersBuilder()
                .addJobParameter("nextOccurrence",new JobParameter<>(date, LocalDate.class))
                .toJobParameters();
        jobLauncher.run(processRecurringTransactions, jobParameters);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
