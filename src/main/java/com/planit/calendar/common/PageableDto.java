package com.planit.calendar.common;

import jakarta.validation.constraints.Min;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

public abstract class PageableDto {
    @Setter
    @Min(value = 0, message = "페이지는 0 이상이어야 합니다.")
    private Integer page;

    @Setter
    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
    private Integer size;

    public PageableDto(Integer page, Integer size) {
        this.page = page;
        this.size = size;
    }

    public abstract Pageable toPageable();

    // page의 기본값은 0
    public Integer getPage() {
        return page == null ? 0 : page;
    }

    // size의 기본값은 10
    public Integer getSize() {
        return size == null ? 10 : size;
    }

}
