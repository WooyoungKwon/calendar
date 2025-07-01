package com.planit.calendar.holiday.dto;

import com.planit.calendar.response.ResponseCode;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HolidaySearchResponse {
    private long totalCount;
    private int totalPageCount;
    private List<HolidayInfoDto> holidays;

    public static HolidaySearchResponse of(long totalCount, int totalPageCount, List<HolidayInfoDto> holidays) {
        return new HolidaySearchResponse(totalCount, totalPageCount, holidays);
    }
}
