package com.planit.calendar.country.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CountryInfoDto {
    private Long countryId;
    private String name;
    private String countryCode;
}
