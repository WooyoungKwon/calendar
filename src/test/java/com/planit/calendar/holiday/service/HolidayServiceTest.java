package com.planit.calendar.holiday.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.planit.calendar.country.domain.Country;
import com.planit.calendar.holiday.domain.Holiday;
import com.planit.calendar.holiday.dto.HolidayDto;
import com.planit.calendar.holiday.dto.HolidayPageableDto;
import com.planit.calendar.holiday.dto.HolidaySearchRequest;
import com.planit.calendar.holiday.dto.HolidaySearchResponse;
import com.planit.calendar.holiday.repository.HolidayRepository;
import com.planit.calendar.response.ResponseCode;
import com.planit.calendar.textFixture.TestFixture;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("HolidayService 단위 테스트")
class HolidayServiceTest {

    @Mock
    private HolidayRepository holidayRepository;

    @InjectMocks
    private HolidayService holidayService;

    @Test
    @DisplayName("HolidayDto 리스트를 Holiday 엔티티로 변환하여 Repository에 저장하고 저장된 Holiday 리스트를 반환한다")
    void 공휴일저장_공휴일_데이터_저장에_성공하고_저장된_공휴일_리스트를_반환한다() {
        // given
        Country country = Country.builder().countryCode("US").name("United States").build();
        List<HolidayDto> mockHolidayDtoList = TestFixture.holidayDtoListFixtureUS();

        // when
        when(holidayRepository.saveAll(anyList())).thenAnswer(
            invocation -> invocation.getArgument(0));
        List<Holiday> savedHolidayList = holidayService.saveHolidays(country, mockHolidayDtoList)
            .block();

        // then
        // saveAll() 메서드를 1번 호출했는가
        verify(holidayRepository, times(1)).saveAll(anyList());

        assertNotNull(savedHolidayList);
        // 저장된 공휴일 수가 mock 데이터와 일치하는가
        assertEquals(3, savedHolidayList.size());
        // 첫 번째 공휴일의 이름과 국가가 mock 데이터와 일치하는가
        assertEquals(mockHolidayDtoList.getFirst().getName(),
            savedHolidayList.getFirst().getName());
        assertEquals(country, savedHolidayList.getFirst().getCountry());
        assertEquals(mockHolidayDtoList.getLast().getTypes(),
            savedHolidayList.getLast().getTypes());
    }

    @Test
    @DisplayName("나라와 날짜 조건에 맞는 공휴일을 페이징 조회하고, 조회된 공휴일 정보를 반환한다")
    public void 공휴일조회_나라_날짜_조건_페이징_조회() throws Exception {
        //given
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").ascending());
        Long countryId = 1L;
        LocalDate beforeYear = LocalDate.of(2020, 1, 1);
        LocalDate afterYear = LocalDate.of(2020, 12, 31);
        HolidaySearchRequest request = HolidaySearchRequest.of(countryId, beforeYear,
            afterYear);

        List<Holiday> mockHolidays = TestFixture.holidayListFixtureKR();

        //when
        PageImpl<Holiday> mockHolidayPage = new PageImpl<>(mockHolidays, pageable,
            mockHolidays.size());
        when(holidayRepository.findByCountry_IdAndDateBetween(
            countryId, beforeYear, afterYear, pageable))
            .thenReturn(mockHolidayPage);

        //then
        HolidaySearchResponse holidaysByConditions = holidayService.getHolidaysByConditions(
            new HolidayPageableDto(page, size), request);

        assertNotNull(holidaysByConditions);
    }
}