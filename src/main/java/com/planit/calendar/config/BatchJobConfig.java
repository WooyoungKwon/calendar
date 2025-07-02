package com.planit.calendar.config;

import com.planit.calendar.scheduler.HolidaySyncTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchJobConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final HolidaySyncTasklet holidaySyncTasklet;

    @Bean
    public Job syncHolidayJob() {
        return new JobBuilder("holidaySyncJob", jobRepository)
                .start(syncHolidayStep())
                .build();
    }

    @Bean
    public Step syncHolidayStep() {
        return new StepBuilder("syncHolidayStep", jobRepository)
                .tasklet(holidaySyncTasklet, transactionManager)
                .build();
    }
}
