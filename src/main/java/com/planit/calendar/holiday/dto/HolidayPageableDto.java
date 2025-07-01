package com.planit.calendar.holiday.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class HolidayPageableDto {
    @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
    private int page = 0;
    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
    private int size = 10;

    public HolidayPageableDto(int page, int size) {
        this.page = page;
        this.size = size;
    }

    private String sortBy = "date";

    public Pageable toPageable() {
        return PageRequest.of(this.page, this.size, Sort.by(this.sortBy).ascending());
    }

}
