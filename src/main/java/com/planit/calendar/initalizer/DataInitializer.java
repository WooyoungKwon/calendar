package com.planit.calendar.initalizer;

import com.planit.calendar.common.YearRange;
import com.planit.calendar.country.domain.Country;
import com.planit.calendar.country.dto.CountryDto;
import com.planit.calendar.country.repository.CountryRepository;
import com.planit.calendar.country.service.CountryService;
import com.planit.calendar.holiday.dto.HolidayDto;
import com.planit.calendar.holiday.repository.HolidayRepository;
import com.planit.calendar.holiday.service.HolidayService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test") // 테스트 환경에서 실행 X
public class DataInitializer {

    private final CountryService countryService;
    private final CountryRepository countryRepository;

    private final HolidayService holidayService;
    private final HolidayRepository holidayRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Order(1)
    public void initCountryList() {
        // 이미 국가 데이터가 존재할 경우 저장 X
        if (countryRepository.count() > 0) {
            log.info("국가 데이터가 이미 존재합니다. 초기 세팅 로직을 실행하지 않습니다.");
            return;
        }
        // 외부 API에서 국가 데이터를 조회
        Mono<List<CountryDto>> fetchCountryMonoList = countryService.fetchCountries();
        // 조회된 국가 데이터를 DB에 저장
        fetchCountryMonoList.flatMap(countryService::saveCountries)
            .doOnSuccess(countryList -> log.info("{}개 국가 데이터를 DB에 저장했습니다.", countryList.size()))
            .doOnError(error -> log.error("국가 데이터를 DB에 저장하는 과정에서 오류가 발생: {}", error.getMessage()))
            .block();
    }

    @EventListener(ApplicationReadyEvent.class)
    @Order(2)
    public void initHolidayList() {
        if (holidayRepository.count() > 0) {
            log.info("공휴일 데이터가 이미 존재합니다. 초기 세팅 로직을 실행하지 않습니다.");
            return;
        }
        // 외부 API에서 공휴일 데이터를 조회
        List<String> yearList = YearRange.getYearRange();
        List<Country> countryList = countryRepository.findAll();

        Flux.fromIterable(yearList)
            .flatMap(year -> Flux.fromIterable(countryList)
                .flatMap(country -> {
                    Mono<List<HolidayDto>> fetchHolidaysMono = holidayService.fetchHolidays(year, country.getCountryCode());
                    return fetchHolidaysMono.flatMap(holidayDto -> holidayService.saveHolidays(country, holidayDto));
                }, 10)
            )
            .subscribeOn(Schedulers.boundedElastic())
            .doOnComplete(() -> log.info("모든 공휴일 데이터를 DB에 저장했습니다."))
            .subscribe();
    }
}
