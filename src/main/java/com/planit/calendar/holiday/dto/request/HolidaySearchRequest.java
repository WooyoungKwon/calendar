package com.planit.calendar.holiday.dto.request;

import com.planit.calendar.common.YearRange;
import jakarta.validation.constraints.AssertTrue;
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

    @AssertTrue(message = "시작 날짜의 연도는 2020년부터 2025년 사이여야 합니다.")
    private boolean isBeforeYearInValidRange() {
        return beforeYear != null && beforeYear.getYear() >= YearRange.START_YEAR.getYear() && beforeYear.getYear() <= YearRange.END_YEAR.getYear();
    }

    @AssertTrue(message = "종료 날짜의 연도는 2020년부터 2025년 사이여야 합니다.")
    private boolean isAfterYearInValidRange() {
        // afterYear가 null이 아닐 때만 검증
        return afterYear != null && afterYear.getYear() >= YearRange.START_YEAR.getYear() && afterYear.getYear() <= YearRange.END_YEAR.getYear();
    }
}
