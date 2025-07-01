package com.planit.calendar.holiday.dto;

import com.planit.calendar.holiday.domain.Holiday;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HolidayInfoDto {
    private Long id;
    private LocalDate date;
    private String localName;
    private String name;
    private boolean fixed;
    private boolean global;
    private List<String> counties;
    private Integer launchYear;
    private List<String> types;

    public static List<HolidayInfoDto> from(List<Holiday> holidayList) {
        return holidayList.stream().map(
            holiday -> new HolidayInfoDto(
                holiday.getId(),
                holiday.getDate(),
                holiday.getLocalName(),
                holiday.getName(),
                holiday.isFixed(),
                holiday.isGlobal(),
                holiday.getCounties(),
                holiday.getLaunchYear(),
                holiday.getTypes()
            )
        ).toList();
    }
}
