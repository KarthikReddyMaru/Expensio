package com.cashigo.expensio.batch.launcher;

import com.cashigo.expensio.common.consts.BudgetRecurrence;
import com.cashigo.expensio.common.documentation.StandardErrorResponses;
import com.cashigo.expensio.common.util.ZoneUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
@RequestMapping("/batch")
@StandardErrorResponses
@Tag(name = "Refresh Transactions/Cycles")
public class BatchLauncher {

    private final JobLauncher jobLauncher;
    private final Job refreshWeeklyBudgets;
    private final Job refreshMonthlyBudgets;
    private final Job processRecurringTransactions;
    private final Job csvErrorFileCleanUp;

    public BatchLauncher(JobLauncher jobLauncher,
                         @Qualifier("refreshWeeklyBudgets") Job refreshWeeklyBudgets,
                         @Qualifier("refreshMonthlyBudgets") Job refreshMonthlyBudgets,
                         @Qualifier("processRecurringTransactions") Job processRecurringTransactions,
                         @Qualifier("csvErrorFileCleanUp") Job csvErrorFileCleanUp
    ) {
        this.jobLauncher = jobLauncher;
        this.refreshWeeklyBudgets = refreshWeeklyBudgets;
        this.refreshMonthlyBudgets = refreshMonthlyBudgets;
        this.processRecurringTransactions = processRecurringTransactions;
        this.csvErrorFileCleanUp = csvErrorFileCleanUp;
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

    @GetMapping("cleanup")
    @Operation(
            summary = "Clean up error csv files",
            parameters = {
                    @Parameter(name = "date", example = "10-10-2020"),
                    @Parameter(name = "time", example = "16:10:03")
            }
    )
    @ApiResponse(responseCode = "200")
    @SneakyThrows
    public ResponseEntity<Void> cleanUpErrorCsvFiles(String date, @RequestParam(required = false) String time) {
        String instant = ZoneUtil.getInstant(date, time).toString();
        log.info("Instant: {}", instant);
        JobParameters jobParameters = new JobParametersBuilder()
                .addJobParameter("now", new JobParameter<>(instant, String.class))
                .toJobParameters();
        jobLauncher.run(csvErrorFileCleanUp, jobParameters);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
