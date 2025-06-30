package com.planit.calendar.holiday;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HolidayDto {
    private String date;
    private String localName;
    private String name;
    private String countryCode;
    private String fixed;
    private String global;
    private List<String> counties;
    private String launchYear;
    private List<String> types;
}
