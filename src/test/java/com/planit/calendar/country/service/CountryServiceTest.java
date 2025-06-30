package com.planit.calendar.country.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.planit.calendar.country.domain.Country;
import com.planit.calendar.country.dto.CountryDto;
import com.planit.calendar.country.repository.CountryRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@DisplayName("CountryService 단위 테스트")
class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CountryService countryService;

    @Test
    @DisplayName("CountryDto 리스트를 Country 엔티티로 변환하여 Repository에 저장하고 저장된 Country 리스트를 반환한다")
    void 국가저장_국가_데이터_저장에_성공하고_저장된_국가_리스트를_반환한다() {
        // given
        List<CountryDto> mockCountryDtoList = Arrays.asList(
            new CountryDto("T1", "testname1"),
            new CountryDto("T2", "testname2"),
            new CountryDto("T3", "testname3")
        );


        // when
        when(countryRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        List<Country> savedCountryList = countryService.saveCountries(mockCountryDtoList).block();

        // then
        // saveAll() 메서드 1번 호출
        verify(countryRepository, times(1)).saveAll(anyList());

        assertNotNull(savedCountryList);
        // 저장된 Country 리스트의 크기와 내용이 mockCountryDtoList와 일치하는가
        assertEquals(mockCountryDtoList.size(), savedCountryList.size());
        // 저장된 Country 리스트의 첫 번째와 마지막 요소의 코드와 이름이 mockCountryDtoList와 일치하는가
        assertEquals(mockCountryDtoList.getFirst().getName(), savedCountryList.getFirst().getName());
        assertEquals(mockCountryDtoList.getLast().getCode(), savedCountryList.getLast().getCode());
    }

}