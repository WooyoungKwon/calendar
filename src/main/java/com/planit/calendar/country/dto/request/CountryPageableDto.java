package com.planit.calendar.country.dto.request;

import com.planit.calendar.common.PageableDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class CountryPageableDto extends PageableDto {
    // 기본 정렬 기준은 날짜로 설정
    private String sortBy = "name";

    public CountryPageableDto(Integer page, Integer size) {
        super(page, size);
    }

    @Override
    public Pageable toPageable() {
        return PageRequest.of(getPage(), getSize(), Sort.by(this.sortBy).ascending());
    }

    public void changeSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
}
