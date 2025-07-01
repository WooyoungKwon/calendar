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
    private int currentCount;
    private List<HolidayInfoDto> holidays;

    public static HolidaySearchResponse of(String countryName, long totalCount, int totalPageCount,
        int currentCount,
        List<HolidayInfoDto> holidays) {
        return new HolidaySearchResponse(countryName, totalCount, totalPageCount, currentCount, holidays);
    }
}
