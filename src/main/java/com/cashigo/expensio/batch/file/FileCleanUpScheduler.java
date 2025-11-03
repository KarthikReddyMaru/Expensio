package com.cashigo.expensio.batch.file;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Instant;

@Slf4j
@Configuration
public class FileCleanUpScheduler {

    private final Job csvErrorFileCleanUp;
    private final JobLauncher jobLauncher;

    public FileCleanUpScheduler(
            @Qualifier("csvErrorFileCleanUp") Job csvErrorFileCleanUp,
            JobLauncher jobLauncher
    ) {
        this.csvErrorFileCleanUp = csvErrorFileCleanUp;
        this.jobLauncher = jobLauncher;
    }

    @SneakyThrows
    @Scheduled(cron = "0 0 1 * * *")
    public void launchFileCleanUp() {
        log.info("CleanUp in progress..");
        JobParameters jobParameters = new JobParametersBuilder()
                .addJobParameter("now", new JobParameter<>(Instant.now().toString(), String.class))
                .toJobParameters();
        jobLauncher.run(csvErrorFileCleanUp, jobParameters);
        log.info("CleanUp completed...");
    }
}
