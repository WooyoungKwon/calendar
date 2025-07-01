package com.planit.calendar.holiday.service;

import com.planit.calendar.country.domain.Country;
import com.planit.calendar.country.repository.CountryRepository;
import com.planit.calendar.exception.custom.NotFoundException;
import com.planit.calendar.holiday.dto.HolidayDto;
import com.planit.calendar.holiday.domain.Holiday;
import com.planit.calendar.holiday.dto.HolidayInfoDto;
import com.planit.calendar.holiday.dto.HolidaySearchRequest;
import com.planit.calendar.holiday.dto.HolidaySearchResponse;
import com.planit.calendar.holiday.repository.HolidayRepository;
import com.planit.calendar.response.ResponseCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    private final WebClient webClient;
    private final HolidayRepository holidayRepository;
    private final CountryRepository countryRepository;

    public Mono<List<HolidayDto>> fetchHolidays(String year, String countryCode) {
        return webClient.get()
            .uri("/api/v3/PublicHolidays/{year}/{countryCode}", year, countryCode)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<HolidayDto>>() {
            })
            .doOnNext(
                holidayDtoList -> log.info("{}개의 공휴일 데이터 목록을 조회했습니다.", holidayDtoList.size()));
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
                    .fixed(holidayDto.isFixed())
                    .global(holidayDto.isGlobal())
                    .counties(holidayDto.getCounties())
                    .launchYear(holidayDto.getLaunchYear())
                    .types(holidayDto.getTypes())
                    .build()
            ).toList();

        // Mono 객체를 별도 스레드에서 실행하며 DB에 벌크 저장
        return Mono.fromCallable(() -> holidayRepository.saveAll(holidayList))
            .subscribeOn(Schedulers.boundedElastic());
    }

    public HolidaySearchResponse getHolidaysByConditions(Pageable pageable,
        HolidaySearchRequest holidaySearchRequest) {

        Country country = countryRepository.findById(holidaySearchRequest.getCountryId())
            .orElseThrow(() -> new NotFoundException("해당 국가를 찾을 수 없습니다."));

        int year = holidaySearchRequest.getBeforeYear().getYear();
        if (year < 2020 || year > 2025) {
            throw new IllegalArgumentException("2020년부터 2025년 사이의 연도만 조회할 수 있습니다.");
        }

        // 나라와 연도 시작, 연도 끝 사이에 있는 공휴일 데이터를 페이징하여 조회
        Page<Holiday> holidayByCountryAndDateList = holidayRepository.findByCountry_IdAndDateBetween(
            holidaySearchRequest.getCountryId(),
            holidaySearchRequest.getBeforeYear(),
            holidaySearchRequest.getAfterYear(), pageable);

        // 조회된 공휴일 데이터를 DTO로 변환
        List<HolidayInfoDto> holidayInfoDtoList = HolidayInfoDto.from(
            holidayByCountryAndDateList.getContent());

        // 페이징된 결과를 응답 객체로 변환하여 반환
        return HolidaySearchResponse.of(country.getName(), holidayByCountryAndDateList.getTotalElements(),
            holidayByCountryAndDateList.getTotalPages(), holidayInfoDtoList);
    }
}
