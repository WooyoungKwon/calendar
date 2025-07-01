package com.planit.calendar.holiday.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@AllArgsConstructor
public class HolidaySearchRequest {
    private Long countryId;
    // 날짜 형식은 ISO 8601(yyyy-mm-dd)을 따른다
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate beforeYear;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate afterYear;

    public static HolidaySearchRequest of(Long countryId, LocalDate beforeYear, LocalDate afterYear) {
        return new HolidaySearchRequest(countryId, beforeYear, afterYear);
    }
}
