package com.planit.calendar.holiday.service;

import com.planit.calendar.country.domain.Country;
import com.planit.calendar.country.repository.CountryRepository;
import com.planit.calendar.holiday.HolidayDto;
import com.planit.calendar.holiday.domain.Holiday;
import com.planit.calendar.holiday.repository.HolidayRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class HolidayService {

    private final CountryRepository countryRepository;
    private final WebClient webClient;
    private final HolidayRepository holidayRepository;

    public Mono<List<HolidayDto>> fetchHolidays(String year, String countryCode) {
        return webClient.get()
            .uri("/api/v3/PublicHolidays/{year}/{countryCode}", year, countryCode)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<HolidayDto>>() {})
            .doOnNext(holidayDtoList -> log.info("{}개의 공휴일 데이터 목록을 조회했습니다.", holidayDtoList.size()));
    }

    @Transactional
    public Mono<List<Holiday>> saveHolidays(Country country, List<HolidayDto> holidayDtoList) {
        // DTO를 엔티티로 변환
        List<Holiday> holidayList = holidayDtoList.stream()
            .map(holidayDto ->
                Holiday.builder()
                    .date(holidayDto.getDate())
                    .localName(holidayDto.getLocalName())
                    .name(holidayDto.getName())
                    .country(country)
                    .fixed(holidayDto.getFixed())
                    .global(holidayDto.getGlobal())
                    .counties(holidayDto.getCounties())
                    .launchYear(holidayDto.getLaunchYear())
                    .types(holidayDto.getTypes())
                    .build()
            ).toList();

        // Mono 객체를 별도 스레드에서 실행하며 DB에 벌크 저장
        return Mono.fromCallable(() -> holidayRepository.saveAll(holidayList))
            .subscribeOn(Schedulers.boundedElastic());
    }

}
