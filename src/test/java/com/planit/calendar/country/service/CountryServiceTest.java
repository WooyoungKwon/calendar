package com.planit.calendar.country.service;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.planit.calendar.country.domain.Country;
import com.planit.calendar.country.dto.CountryDto;
import com.planit.calendar.country.repository.CountryRepository;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest
@ActiveProfiles("test")
class CountryServiceTest {
    private MockWebServer mockWebServer;

    @Autowired
    private CountryRepository countryRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private WebClient webClient;
    @Autowired
    private CountryService countryService;

    @BeforeEach
    void setUp() throws IOException {
        // MockWebServer 인스턴스 생성
        mockWebServer = new MockWebServer();
        // MockWebServer를 시작하고 포트를 할당
        mockWebServer.start(3459);

        String baseUrl = String.format("http://localhost:" + mockWebServer.getPort());
        webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    @AfterEach
    void tearDown() throws IOException {
        // MockWebServer 종료 (리소스 해제)
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("WebClient로 조회한 데이터가 Country 테이블에 저장된다")
    public void save_countries_from_webClient() throws Exception {
        //given
        // MockWebServer의 응답 설정
        List<CountryDto> mockCountryDtoList = Arrays.asList(
            new CountryDto("T1", "testname1"),
            new CountryDto("T2", "testname2"),
            new CountryDto("T3", "testname3")
        );
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(objectMapper.writeValueAsString(mockCountryDtoList)));

        // 국가를 저장하기 전에는 DB에 데이터가 없으므로 count는 0
        long initialCount = countryRepository.count();
        assertEquals(0, initialCount);

        //when
        // MockWebServer에 가짜 데이터 요청
        List<CountryDto> countryDtoList = webClient.get()
            .uri("/api/v3/AvailableCountries")
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<CountryDto>>() {
            })
            .block();

        // 국가 데이터 조회 후 저장
        List<Country> block = countryService.saveCountries(countryDtoList).block();


        //then
        assertEquals(1, mockWebServer.getRequestCount()); // MockWebServer에 요청이 1번 발생했는지 확인
        assertEquals(mockCountryDtoList.size(), countryRepository.count());
    }

}