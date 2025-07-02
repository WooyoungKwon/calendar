package com.planit.calendar.holiday.dto.response;

import com.planit.calendar.holiday.dto.HolidayInfoDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class HolidaySearchResponse {

    private String condition;
    private long totalCount;
    private int totalPageCount;
    private int currentCount;
    private List<? extends HolidayInfoDto> holidays;

    public static HolidaySearchResponse of(String countryName, long totalCount, int totalPageCount,
        int currentCount, List<? extends HolidayInfoDto> holidays) {
        return new HolidaySearchResponse(countryName, totalCount, totalPageCount, currentCount, holidays);
    }
}
