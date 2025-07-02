package com.planit.calendar.holiday.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HolidaySearchByCountryRequest {
    private Long countryId;
}
