package com.planit.calendar.country.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Country {

    @Id @GeneratedValue
    @Column(name = "country_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "country_name", nullable = false, unique = true)
    private String name;

    @Column(name = "country_code", nullable = false, unique = true)
    private String code;

    @Builder
    public Country(String name, String code) {
        if (code == null || code.length() != 2) {
            throw new IllegalArgumentException("국가 코드는 반드시 2글자여야 하며 비어있을 수 없습니다.");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("국가 이름은 비어있을 수 없습니다.");
        }
        this.name = name;
        this.code = code.toUpperCase();
    }

}
