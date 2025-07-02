package com.planit.calendar.holiday.service;

import com.planit.calendar.common.PageableDto;
import com.planit.calendar.common.YearRange;
import com.planit.calendar.country.domain.Country;
import com.planit.calendar.country.dto.response.ChangeType;
import com.planit.calendar.country.dto.response.ChangedDataCount;
import com.planit.calendar.country.dto.response.CountryInfoDto;
import com.planit.calendar.country.dto.response.CountryListDto;
import com.planit.calendar.country.repository.CountryRepository;
import com.planit.calendar.exception.custom.NotFoundException;
import com.planit.calendar.holiday.dto.HolidayDto;
import com.planit.calendar.holiday.domain.Holiday;
import com.planit.calendar.holiday.dto.HolidayInfoDto;
import com.planit.calendar.holiday.dto.response.HolidayInfoWithCountry;
import com.planit.calendar.holiday.dto.request.HolidayPageableDto;
import com.planit.calendar.holiday.dto.request.HolidaySearchByCountryRequest;
import com.planit.calendar.holiday.dto.request.HolidayByYearRequest;
import com.planit.calendar.holiday.dto.request.HolidaySearchRequest;
import com.planit.calendar.holiday.dto.response.HolidaySearchResponse;
import com.planit.calendar.holiday.repository.HolidayRepository;
import com.planit.calendar.response.ResponseCode;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    /**
     * 연도와 국가 코드로 외부 API를 통해 공휴일 데이터 조회
     */
    public Mono<List<HolidayDto>> fetchHolidays(int year, String countryCode) {
        return webClient.get()
            .uri("/api/v3/PublicHolidays/{year}/{countryCode}", year, countryCode)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<HolidayDto>>() {
            })
            .doOnNext(
                holidayDtoList -> log.info("{}개의 공휴일 데이터 목록을 조회했습니다.", holidayDtoList.size()));
    }

    /**
     * 외부 API를 국가와 연도 기준으로 공휴일 데이터 조회
     */
    private List<HolidayDto> fetchHolidayByCountryYear(String countryCode, int year) {
        log.info("국가코드: {} 연도: {}년 데이터 외부 API 요청합니다.", countryCode, year);

        return webClient.get()
            .uri("/api/v3/PublicHolidays/{year}/{countryCode}", year, countryCode)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<HolidayDto>>() {
            })
            .doOnNext(holidayDtoList -> log.info("국가코드: {}, {}개의 공휴일 데이터를 조회합니다.", countryCode,
                holidayDtoList.size()))
            .block();
    }

    /**
     * 저장된 국가 목록 조회
     */
    public CountryListDto getAllCountries(PageableDto pageableDto) {
        Pageable pageable = PageRequest.of(pageableDto.getPage(), pageableDto.getSize());
        Page<Country> countryList = countryRepository.findAll(pageable);
        List<CountryInfoDto> countryInfoDtoList = countryList
            .stream().map(
                country -> CountryInfoDto.builder()
                    .countryId(country.getId())
                    .name(country.getName())
                    .countryCode(country.getCountryCode())
                    .build()
            ).toList();
        return CountryListDto.builder()
            .currentCount(countryList.getNumberOfElements())
            .totalCount(countryList.getTotalElements())
            .totalPageCount(countryList.getTotalPages())
            .countryList(countryInfoDtoList)
            .build();
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

    /**
     * 나라와 연도 시작, 연도 끝 사이에 있는 공휴일 데이터 조회
     */
    public HolidaySearchResponse getHolidaysByConditions(HolidayPageableDto holidayPageableDto,
        HolidaySearchRequest holidaySearchRequest) {

        Country country = countryRepository.findById(holidaySearchRequest.getCountryId())
            .orElseThrow(() -> new NotFoundException(ResponseCode.COUNTRY_NOT_FOUND.getMessage()));

        Pageable pageable = holidayPageableDto.toPageable();

        // 나라와 연도 시작, 연도 끝 사이에 있는 공휴일 데이터를 페이징하여 조회
        Page<Holiday> holidayByCountryAndDateList = holidayRepository.findByCountry_IdAndDateBetween(
            holidaySearchRequest.getCountryId(),
            holidaySearchRequest.getBeforeYear(),
            holidaySearchRequest.getAfterYear(), pageable);

        // 조회된 공휴일 데이터를 DTO로 변환
        List<HolidayInfoDto> holidayInfoDtoList = HolidayInfoDto.from(
            holidayByCountryAndDateList.getContent());

        String condition = String.format("나라: %s, 날짜 범위: %s ~ %s",
            country.getName(), holidaySearchRequest.getBeforeYear(),
            holidaySearchRequest.getAfterYear());

        // 페이징된 결과를 응답 객체로 변환하여 반환
        return HolidaySearchResponse.of(condition, holidayByCountryAndDateList.getTotalElements(),
            holidayByCountryAndDateList.getTotalPages(), holidayInfoDtoList.size(),
            holidayInfoDtoList);
    }

    /**
     * 연도별 공휴일 데이터 조회
     */
    public HolidaySearchResponse getHolidaysByYear(HolidayPageableDto holidayPageableDto,
        HolidayByYearRequest request) {
        Pageable pageable = holidayPageableDto.toPageable();

        Page<HolidayInfoWithCountry> holidayInfoDtoList = holidayRepository.findByYear(
            request.getYear(), pageable);

        String condition = String.format("연도: %d년", request.getYear());

        return HolidaySearchResponse.of(condition, holidayInfoDtoList.getTotalElements(),
            holidayInfoDtoList.getTotalPages(), holidayInfoDtoList.getNumberOfElements(),
            holidayInfoDtoList.getContent());
    }

    /**
     * 국가별 공휴일 데이터 조회
     */
    public HolidaySearchResponse getHolidaysByCountry(HolidayPageableDto holidayPageableDto,
        HolidaySearchByCountryRequest request) {

        Country country = countryRepository.findById(request.getCountryId())
            .orElseThrow(() -> new NotFoundException(ResponseCode.COUNTRY_NOT_FOUND.getMessage()));

        Pageable pageable = holidayPageableDto.toPageable();
        Page<Holiday> holidayPage = holidayRepository.findByCountry_Id(request.getCountryId(),
            pageable);

        List<HolidayInfoDto> holidayInfoDtoList = HolidayInfoDto.from(holidayPage.getContent());

        return HolidaySearchResponse.of(country.getName(), holidayPage.getTotalElements(),
            holidayPage.getTotalPages(), holidayInfoDtoList.size(),
            holidayInfoDtoList);
    }

    /**
     * 외부 API를 통해 국가와 연도별 공휴일 데이터 재동기화
     */
    @Transactional
    public ChangedDataCount synchronizeByCountryAndYear(Long countryId, int year) {
        Country country = countryRepository.findById(countryId)
            .orElseThrow(
                () -> new IllegalArgumentException(ResponseCode.COUNTRY_NOT_FOUND.getMessage()));

        log.info("국가: {}, 연도: {}년 공휴일 데이터 재동기화 시작", country.getName(), year);

        // 변경 데이터 개수를 저장할 객체 생성
        ChangedDataCount changedDataCount = new ChangedDataCount();

        // 현재 DB에 저장되어 있는 해당 국가의 연도별 공휴일 목록 조회
        List<Holiday> holidayList = holidayRepository.findByCountryAndYear(countryId, year);
        // 재동기화
        synchronizeHolidayData(holidayList, country.getCountryCode(), year, changedDataCount);

        log.info("국가: {}, 연도: {}, 총 추가 데이터 개수: {}, 수정 데이터 개수: {}, 삭제 데이터 개수: {}",
            country.getName(), year, changedDataCount.getTotalAddCount(),
            changedDataCount.getTotalUpdateCount(), changedDataCount.getTotalDeleteCount());

        return changedDataCount;
    }

    /**
     * 외부 API를 통해 국가별 공휴일 데이터 재동기화
     */
    @Transactional
    public ChangedDataCount synchronizeByCountry(Long countryId) {

        Country country = countryRepository.findById(countryId)
            .orElseThrow(
                () -> new IllegalArgumentException(ResponseCode.COUNTRY_NOT_FOUND.getMessage()));

        log.info("국가별 공휴일 데이터 재동기화 시작: {}", country.getName());

        // 변경 데이터 개수를 저장할 객체 생성
        ChangedDataCount changedDataCount = new ChangedDataCount();

        for (int year : YearRange.getYearRange()) {
            // 현재 DB에 저장되어 있는 국가와 연도별 공휴일 목록 조회
            List<Holiday> holidayList = holidayRepository.findAllByCountryAndDate(countryId, year);
            // 재동기화
            synchronizeHolidayData(holidayList, country.getCountryCode(), year, changedDataCount);
        }
        log.info("국가: {}, 총 추가 데이터 개수: {}, 수정 데이터 개수: {}, 삭제 데이터 개수: {}",
            country.getName(), changedDataCount.getTotalAddCount(),
            changedDataCount.getTotalUpdateCount(), changedDataCount.getTotalDeleteCount());

        return changedDataCount;
    }

    /**
     * 외부 API를 통해 연도별 공휴일 데이터 재동기화
     */
    @Transactional
    public ChangedDataCount synchronizeByYear(int year) {
        log.info("연도별 공휴일 데이터 재동기화 시작: {}년", year);

        // 변경 데이터 개수를 저장할 객체 생성
        ChangedDataCount changedDataCount = new ChangedDataCount();

        // 저장돼 있는 모든 국가의 공휴일 데이터 동기화
        for (String countryCode : countryRepository.getAllCountryCode()) {
            // 현재 DB에 저장되어 있는 국가와 연도별 공휴일 목록 조회
            List<Holiday> holidayList = holidayRepository.findByYearAndCountryCode(year,
                countryCode);
            // 재동기화
            synchronizeHolidayData(holidayList, countryCode, year, changedDataCount);
        }
        log.info("연도: {}, 총 추가 데이터 개수: {}, 수정 데이터 개수: {}, 삭제 데이터 개수: {}",
            year, changedDataCount.getTotalAddCount(), changedDataCount.getTotalUpdateCount(),
            changedDataCount.getTotalDeleteCount());

        return changedDataCount;
    }

    /**
     * 국가별 공휴일 데이터를 외부 API를 통해 덮어씌우기
     */
    private void synchronizeHolidayData(List<Holiday> holidayList, String countryCode, int year,
        ChangedDataCount changedDataCount) {
        // 날짜를 키로 하는 공휴일 Map 생성
        Map<String, Holiday> holidayMap = listCovertMap(holidayList);
        // 기존 데이터와 새로운 데이터의 비교를 위한 Set
        Set<String> newYearSet = new HashSet<>();
        // 외부 API를 통해 동기화 할 새로운 공휴일 데이터 조회
        List<HolidayDto> newHolidayDtoList = fetchHolidayByCountryYear(countryCode, year);

        for (HolidayDto newHolidayDto : newHolidayDtoList) {
            String key = String.format("%s-%s-%s-%s", newHolidayDto.getDate(),
                newHolidayDto.getLocalName(), newHolidayDto.getName(), newHolidayDto.getTypes());
            newYearSet.add(key);
            Holiday existHoliday = holidayMap.get(key);

            // 기존에 존재하는 공휴일 업데이트
            if (existHoliday != null) {
                existHoliday.update(newHolidayDto.toEntity());
                // 변경된 공휴일 데이터 개수 저장
                changedDataCount.incrementTotalCount(ChangeType.UPDATE);
            }
            // 새로 추가된 공휴일
            else {
                Holiday newHoliday = newHolidayDto.toEntity();
                holidayRepository.save(newHoliday);
                log.info("새로운 공휴일 데이터 저장: {}", newHoliday.getName());
                // 새로 추가된 공휴일 데이터 개수 저장
                changedDataCount.incrementTotalCount(ChangeType.ADD);
            }
        }
        // 기존에 존재했지만 새로 받은 데이터에는 없는 공휴일 삭제
        for (Entry<String, Holiday> entry : holidayMap.entrySet()) {
            if (!newYearSet.contains(entry.getKey())) {
                holidayRepository.delete(entry.getValue());
                log.info("기존 공휴일 데이터 삭제: {}", entry.getValue().getName());
                // 삭제된 공휴일 데이터 개수 저장
                changedDataCount.incrementTotalCount(ChangeType.DELETE);
            }
        }
    }

    // 날짜를 키 값으로 하는 Map 자료구조로 변경
    private Map<String, Holiday> listCovertMap(List<Holiday> holidayList) {
        return holidayList
            .stream()
            .collect(Collectors.toMap(
                holiday -> String.format("%s-%s-%s-%s", holiday.getDate(), holiday.getLocalName(),
                    holiday.getName(), holiday.getTypes()),
                Function.identity()
            ));
    }

    @Transactional
    public void deleteAllByCountry(Long countryId) {
        Country country = countryRepository.findById(countryId)
            .orElseThrow(() -> new NotFoundException(ResponseCode.COUNTRY_NOT_FOUND.getMessage()));

        log.info("{} 국가의 모든 공휴일 데이터를 삭제합니다.", country.getName());

        holidayRepository.deleteByCountry_Id(countryId);

        log.info("{} 국가의 모든 공휴일 데이터 삭제 완료", country.getName());
    }

    @Transactional
    public void deleteAllByYear(int year) {
        log.info("{}년의 모든 공휴일 데이터를 삭제합니다.", year);

        holidayRepository.deleteByDate(year);

        log.info("{}년의 모든 공휴일 데이터 삭제 완료", year);
    }

    @Transactional
    public void deleteAllByCountryAndYear(Long countryId, String year) {
        Country country = countryRepository.findById(countryId)
            .orElseThrow(() -> new NotFoundException(ResponseCode.COUNTRY_NOT_FOUND.getMessage()));

        log.info("{} 국가의 {}년 공휴일 데이터를 삭제합니다.", country.getName(), year);

        holidayRepository.deleteByCountryAndYear(countryId, year);

        log.info("{} 국가의 {}년 공휴일 데이터 삭제 완료", country.getName(), year);
    }
}
