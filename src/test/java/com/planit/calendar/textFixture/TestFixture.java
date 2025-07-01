package com.planit.calendar.textFixture;

import com.planit.calendar.country.domain.Country;
import com.planit.calendar.country.dto.CountryDto;
import com.planit.calendar.holiday.domain.Holiday;
import com.planit.calendar.holiday.dto.HolidayDto;
import java.util.List;

public class TestFixture {

    public static List<CountryDto> countryDtoListFixtureTest() {
         return List.of(
            new CountryDto("T1", "testname1"),
            new CountryDto("T2", "testname2"),
            new CountryDto("T3", "testname3")
        );
    }
    public static List<HolidayDto> holidayDtoListFixtureUS() {
         return List.of(
            new HolidayDto(
                "2025-01-01",
                "New Year's Day",
                "New Year's Day",
                "US",
                true,
                true,
                null,
                null,
                List.of("Public")
            ),
            new HolidayDto(
                "2025-01-20",
                "Martin Luther King, Jr. Day",
                "Martin Luther King, Jr. Day",
                "US",
                false,
                true,
                null,
                null,
                List.of("Public")
            ),
            new HolidayDto(
                "2025-02-12",
                "Lincoln's Birthday",
                "Lincoln's Birthday",
                "US",
                false,
                false,
                List.of("US-CA", "US-CT", "US-IL", "US-IN", "US-KY", "US-MI", "US-NY", "US-MO",
                    "US-OH"),
                null,
                List.of("Observance")
            )
        );
    }

    public static List<Holiday> holidayListFixtureKR() {
        Country korea = Country.builder().countryCode("KR").name("South Korea").build();
        return List.of(
            Holiday.testObjWithId()
                .id(101L) // ID 예시
                .date("2025-01-01")
                .localName("새해")
                .name("New Year's Day")
                .fixed(false) // JSON 데이터에 따라 false 유지
                .global(true)
                .counties(null)
                .launchYear(null) // "null" 문자열이 아니라 실제 null 값으로
                .types(List.of("Public")) // List.of() 사용
                .country(korea)
                .build(),
            Holiday.testObjWithId()
                .id(102L) // ID 예시
                .date("2025-01-28")
                .localName("설날")
                .name("Lunar New Year")
                .fixed(false)
                .global(true)
                .counties(null)
                .launchYear(null)
                .types(List.of("Public"))
                .country(korea)
                .build(),
            Holiday.testObjWithId()
                .id(103L) // ID 예시
                .date("2025-01-29")
                .localName("설날")
                .name("Lunar New Year")
                .fixed(false)
                .global(true)
                .counties(null)
                .launchYear(null)
                .types(List.of("Public"))
                .country(korea)
                .build()
        );
    }

}
