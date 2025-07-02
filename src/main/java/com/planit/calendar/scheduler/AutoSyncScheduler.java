package com.planit.calendar.scheduler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoSyncScheduler {

    private final JobLauncher jobLauncher;
    private final Job holidaySyncJob;

    // 매년 1월 2일 01:00 실행
    @Scheduled(cron = "* * 1 2 1 *", zone = "Asia/Seoul")
    public void runHolidaySyncJob() {
        try {
            int currentYear = LocalDateTime.now(ZoneId.of("Asia/Seoul")).getYear();
            int previousYear = currentYear - 1;

            JobParameters jobParameters = new JobParametersBuilder()
                .addString("currentYear", String.valueOf(currentYear))
                .addString("previousYear", String.valueOf(previousYear))
                .addString("runTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")))
                .toJobParameters();

            log.info( "전년도 {}년과 금년도 {}년의 데이터 동기화 작업 시작", previousYear, currentYear);
            jobLauncher.run(holidaySyncJob, jobParameters);
        } catch (Exception e) {
            log.info("데이터 동기화 작업 시작 중 오류: {}", e.getMessage());
        }
    }

}
