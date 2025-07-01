package com.planit.calendar.holiday.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@AllArgsConstructor
public class HolidayPageableDto {
    @Min(value = 0, message = "페이지는 0 이상이어야 합니다.")
    private Integer page;

    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
    private Integer size;

    // 기본 정렬 기준은 날짜로 설정
    private String sortBy;

    public Pageable toPageable() {
        return PageRequest.of(getPage(), getSize(), Sort.by(getSortBy()).ascending());
    }

    public void changeSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    // page의 기본값은 0
    public Integer getPage() {
        return page == null ? 0 : page;
    }

    // size의 기본값은 10
    public Integer getSize() {
        return size == null ? 10 : size;
    }

    // 정렬 기준의 기본값은 날짜
    public String getSortBy() {
        return sortBy == null ? "date" : sortBy;
    }
}
