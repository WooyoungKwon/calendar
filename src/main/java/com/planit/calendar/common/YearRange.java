package com.planit.calendar.common;

import java.util.ArrayList;
import java.util.List;

public enum YearRange {
    START_YEAR(2020),
    END_YEAR(2025);

    private final int year;

    YearRange(int year) {
        this.year = year;
    }

    public int getYear() {
        return year;
    }

    public static List<String> getYearRange() {
        List<String> yearRange = new ArrayList<>();
        for (int i = START_YEAR.year; i <= END_YEAR.year; i++) {
            yearRange.add(String.valueOf(i));
        }
        return yearRange;
    }
}
