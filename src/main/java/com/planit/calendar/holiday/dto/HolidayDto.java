package com.planit.calendar.holiday.dto;

import com.planit.calendar.country.domain.Country;
import com.planit.calendar.holiday.domain.Holiday;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HolidayDto {
    private String date;
    private String localName;
    private String name;
    private String countryCode;
    private boolean fixed;
    private boolean global;
    private List<String> counties;
    private String launchYear;
    private List<String> types;

    public Holiday toEntity() {
        return Holiday.builder()
            .date(date)
            .localName(localName)
            .name(name)
            .fixed(fixed)
            .global(global)
            .counties(counties)
            .launchYear(launchYear)
            .types(types)
            .build();
    }
}
