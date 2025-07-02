package com.planit.calendar.country.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryListDto {
    int currentCount;
    long totalCount;
    int totalPageCount;
    private List<CountryInfoDto> countryList;
}
