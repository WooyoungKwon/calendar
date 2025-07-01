package com.planit.calendar.holiday.dto;

import com.planit.calendar.response.ResponseCode;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HolidaySearchResponse {

    private String countryName;
    private long totalCount;
    private int totalPageCount;
    private List<HolidayInfoDto> holidays;

    public static HolidaySearchResponse of(String countryName, long totalCount, int totalPageCount,
        List<HolidayInfoDto> holidays) {
        return new HolidaySearchResponse(countryName, totalCount, totalPageCount, holidays);
    }
}
