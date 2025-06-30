package com.planit.calendar.holiday.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.planit.calendar.country.domain.Country;
import com.planit.calendar.country.repository.CountryRepository;
import com.planit.calendar.holiday.domain.Holiday;
import com.planit.calendar.holiday.dto.HolidayDto;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("HolidayRepository 통합 테스트")
class HolidayRepositoryTest {

    @Autowired private HolidayRepository holidayRepository;
    @Autowired private CountryRepository countryRepository;

    @Test
    @DisplayName("공휴일 데이터를 저장하고 조회한다")
    public void 공휴일_데이터_저장() throws Exception {
        //given
        Country country = new Country("South Korea", "KR");
        Country saveCountry = countryRepository.save(country);
        Holiday holiday = new Holiday(
            "2025-01-01",
            "New Year's Day",
            "New Year's Day",
            true,
            true,
            null,
            null,
            List.of("Public"),
            saveCountry
        );

        //when
        Holiday savedHoliday = holidayRepository.save(holiday);
        Holiday findHoliday = holidayRepository.findById(savedHoliday.getId()).get();

        //then
        assertNotNull(findHoliday);
        assertEquals(savedHoliday.getId(), findHoliday.getId());
        assertEquals(holiday.getName(), findHoliday.getName());
        assertEquals(holiday.getDate(), findHoliday.getDate());

    }

}