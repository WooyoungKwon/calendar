package com.planit.calendar.scheduler;

import com.planit.calendar.holiday.service.HolidayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class HolidaySyncTasklet implements Tasklet {

    private final HolidayService holidayService;

    @Value("#{jobParameters['previousYear']}")
    private String previousYear;

    @Value("#{jobParameters['currentYear']}")
    private String currentYear;


    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("{}년과 {}년의 공휴일을 동기화하기 위한 Tasklet 시작", previousYear, currentYear);

        holidayService.synchronizeByYear(previousYear);
        holidayService.synchronizeByYear(currentYear);

        return RepeatStatus.FINISHED;
    }
}
