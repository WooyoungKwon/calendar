package com.planit.calendar.country.dto.request;

import com.planit.calendar.common.YearRange;
import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CountryByYearRequest {

    private Long countryId;
    private int year;

    @AssertTrue(message = "요청 가능한 연도는 2020년부터 2025년 사이여야 합니다.")
    private boolean isBeforeYearInValidRange() {
        return year >= YearRange.START_YEAR.getYear() && year <= YearRange.END_YEAR.getYear();
    }

}
