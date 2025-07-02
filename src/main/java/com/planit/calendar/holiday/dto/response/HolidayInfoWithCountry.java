package com.planit.calendar.holiday.dto.response;

import com.planit.calendar.holiday.dto.HolidayInfoDto;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;

@Getter
public class HolidayInfoWithCountry extends HolidayInfoDto {
    private final String countryName;

    public HolidayInfoWithCountry(Long id, LocalDate date, String localName, String name,
        boolean fixed, boolean global, List<String> counties, Integer launchYear,
        List<String> types, String countryName) {
        super(id, date, localName, name, fixed, global, counties, launchYear, types);
        this.countryName = countryName;
    }
}
