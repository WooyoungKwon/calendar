package com.planit.calendar.holiday.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HolidaySearchRequest {
    private Long countryId;
    private LocalDate beforeYear;
    private LocalDate afterYear;

    public static HolidaySearchRequest of(Long countryId, LocalDate beforeYear, LocalDate afterYear) {
        return new HolidaySearchRequest(countryId, beforeYear, afterYear);
    }
}
