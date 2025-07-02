package com.planit.calendar.holiday.dto.request;

import jakarta.validation.constraints.Min;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@NoArgsConstructor
public class HolidayPageableDto {
    @Setter
    @Min(value = 0, message = "페이지는 0 이상이어야 합니다.")
    private Integer page;

    @Setter
    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
    private Integer size;

    // 기본 정렬 기준은 날짜로 설정
    private String sortBy = "date";

    public HolidayPageableDto(Integer page, Integer size) {
        this.page = page;
        this.size = size;
    }

    public Pageable toPageable() {
        return PageRequest.of(getPage(), getSize(), Sort.by(this.sortBy).ascending());
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
}
