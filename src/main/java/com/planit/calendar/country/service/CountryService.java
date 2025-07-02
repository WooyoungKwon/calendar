package com.planit.calendar.country.service;

import com.planit.calendar.common.YearRange;
import com.planit.calendar.country.domain.Country;
import com.planit.calendar.country.dto.CountryDto;
import com.planit.calendar.country.repository.CountryRepository;
import com.planit.calendar.holiday.domain.Holiday;
import com.planit.calendar.holiday.dto.HolidayDto;
import com.planit.calendar.holiday.repository.HolidayRepository;
import com.planit.calendar.response.ResponseCode;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CountryService {

    private final WebClient webClient;
    private final CountryRepository countryRepository;

    private final HolidayRepository holidayRepository;

    public Mono<List<CountryDto>> fetchCountries() {
        log.info("외부 API에서 국가 데이터 목록을 조회합니다.");
        return webClient.get()
            .uri("/api/v3/AvailableCountries")
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<CountryDto>>() {
            })
            .doOnNext(countryDtoList -> log.info("{}개의 국가 데이터 목록을 조회했습니다.", countryDtoList.size()));
    }

    @Transactional
    public Mono<List<Country>> saveCountries(List<CountryDto> countryDtoList) {
        log.info("조회된 {}개 국가 데이터를 DB에 저장합니다.", countryDtoList.size());

        // DTO를 엔티티로 변환하며 중복값을 제거한다
        List<Country> countryList = countryDtoList.stream()
            .map(countryDto ->
                Country.builder()
                    .countryCode(countryDto.getCountryCode())
                    .name(countryDto.getName())
                    .build()
            ).distinct().toList();

        // Mono 객체를 별도 스레드에서 실행하며 DB에 벌크 저장
        return Mono.fromCallable(() -> countryRepository.saveAll(countryList))
            .subscribeOn(Schedulers.boundedElastic());
    }

    @Transactional
    public void synchronizeCountry(Long countryId) {

        Country country = countryRepository.findById(countryId)
            .orElseThrow(() -> new IllegalArgumentException(ResponseCode.COUNTRY_NOT_FOUND.getMessage()));

        log.info("국가 데이터 재동기화 시작: {}", country.getName());

        int totalAddCount = 0;
        int totalUpdateCount = 0;
        int totalDeleteCount = 0;

        // 중복 제거를 위한 Set
        for (String year : YearRange.getYearRange()) {
            int addCount = 0;
            int updateCount = 0;
            int deleteCount = 0;
            // 현재 DB에 저장되어 있는 국가와 연도별 공휴일 목록 조회
            List<Holiday> holidayList = holidayRepository.findAllByCountryAndDate(countryId, year);
            // 날짜를 키로 하는 공휴일 Map 생성
            Map<String, Holiday> holidayMap = listCovertMap(holidayList);

            // 기존 데이터와 새로운 데이터의 비교를 위한 Set
            Set<String> newYearSet = new HashSet<>();
            // 외부 API를 통해 동기화 할 새로운 공휴일 데이터 조회
            List<HolidayDto> newHolidayDtoList = synchronizeHoliday(country.getCountryCode(), year);

            for (HolidayDto newHolidayDto : newHolidayDtoList) {
                String key = newHolidayDto.getDate();
                newYearSet.add(key);
                Holiday existHoliday = holidayMap.get(key);

                // 기존에 존재하는 공휴일 업데이트
                if (existHoliday != null) {
                    existHoliday.update(newHolidayDto.toEntity());
                    updateCount++;
                }
                // 새로 추가된 공휴일
                else {
                    Holiday newHoliday = newHolidayDto.toEntity();
                    holidayRepository.save(newHoliday);
                    log.info("새로운 공휴일 데이터 저장: {}", newHoliday.getName());
                    addCount++;
                }
            }

            // 기존에 존재했지만 새로 받은 데이터에는 없는 공휴일 삭제
            for (Entry<String, Holiday> entry : holidayMap.entrySet()) {
                if (!newYearSet.contains(entry.getKey())) {
                    holidayRepository.delete(entry.getValue());
                    log.info("기존 공휴일 데이터 삭제: {}", entry.getValue().getName());
                    deleteCount++;
                }
            }
            totalAddCount += addCount;
            totalUpdateCount += updateCount;
            totalDeleteCount += deleteCount;
            log.info("국가: {}, 연도: {}, 추가 데이터 개수: {}, 수정 데이터 개수: {}, 삭제 데이터 개수: {}",
                country.getName(), year, addCount, updateCount, deleteCount);
        }
        log.info("국가: {}, 총 추가 데이터 개수: {}, 수정 데이터 개수: {}, 삭제 데이터 개수: {}",
            country.getName(), totalAddCount, totalUpdateCount, totalDeleteCount);
    }

    // 날짜를 키 값으로 하는 Map 자료구조로 변경
    private Map<String, Holiday> listCovertMap(List<Holiday> holidayList) {
        return holidayList
            .stream()
            .collect(Collectors.toMap(
                holiday -> holiday.getDate().toString(),
                Function.identity()
            ));
    }

    private List<HolidayDto> synchronizeHoliday(String countryCode, String year) {
        log.info("국가코드: {} 연도: {}년 데이터 외부 API 요청", countryCode, year);

        return webClient.get()
            .uri("/api/v3/PublicHolidays/{year}/{countryCode}", year, countryCode)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<HolidayDto>>() {
            })
            .doOnNext(holidayDtoList -> log.info("국가코드: {}, {}개의 공휴일 데이터를 조회했습니다.", countryCode,
                holidayDtoList.size()))
            .block();
    }
}
