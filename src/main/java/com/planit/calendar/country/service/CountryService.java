package com.planit.calendar.country.service;

import com.planit.calendar.country.domain.Country;
import com.planit.calendar.country.dto.CountryDto;
import com.planit.calendar.country.repository.CountryRepository;
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
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CountryService {

    private final WebClient webClient;
    private final CountryRepository countryRepository;

    public Mono<List<CountryDto>> fetchCountries() {
        log.info("외부 API에서 국가 데이터 목록을 조회합니다.");
        return webClient.get()
            .uri("/api/v3/AvailableCountries")
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<CountryDto>>() {})
            .doOnNext(countryDtoList -> log.info("{}개의 국가 데이터 목록을 조회했습니다.", countryDtoList.size()));
    }

    @Transactional
    public Mono<List<Country>> saveCountries(List<CountryDto> countryDtoList) {
        log.info("조회된 {}개 국가 데이터를 DB에 저장합니다.", countryDtoList.size());

        // DTO를 엔티티로 변환
        List<Country> countryList = countryDtoList.stream()
            .map(countryDto ->
                Country.builder()
                    .code(countryDto.getCode())
                    .name(countryDto.getName())
                    .build()
            ).toList();

        // Mono 객체를 별도 스레드에서 실행하며 DB에 벌크 저장
        return Mono.fromCallable(() -> countryRepository.saveAll(countryList))
            .subscribeOn(Schedulers.boundedElastic());
    }
}
