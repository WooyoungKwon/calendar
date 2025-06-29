package com.planit.calendar.common;

import com.planit.calendar.country.dto.CountryDto;
import com.planit.calendar.country.repository.CountryRepository;
import com.planit.calendar.country.service.CountryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test") // 테스트 환경에서는 실행 X
public class DataInitializer {

    private final CountryService countryService;
    private final CountryRepository countryRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        // 이미 국가 데이터가 존재할 경우 저장 X
        if (countryRepository.count() > 0) {
            log.info("국가 데이터가 이미 존재합니다. 초기 세팅 로직을 실행하지 않습니다.");
            return;
        }
        // 외부 API에서 국가 데이터를 조회
        Mono<List<CountryDto>> fetchCountryMonoList = countryService.fetchAvailableCountries();
        // 조회된 국가 데이터를 DB에 저장
        fetchCountryMonoList.flatMap(countryService::saveCountries)
            .doOnSuccess(countryList -> log.info("{}개 국가 데이터를 DB에 저장했습니다.", countryList.size()))
            .doOnError(error -> log.error("국가 데이터를 DB에 저장하는 과정에서 오류가 발생: {}", error.getMessage()))
            .subscribe();
    }
}
