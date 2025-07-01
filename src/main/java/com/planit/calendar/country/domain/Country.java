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
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "country_code", nullable = false, unique = true)
    private String countryCode;

    @Builder
    public Country(String name, String countryCode) {
        if (countryCode == null || countryCode.length() != 2) {
            throw new IllegalArgumentException("국가 코드는 반드시 2글자여야 하며 비어있을 수 없습니다.");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("국가 이름은 비어있을 수 없습니다.");
        }
        this.name = name;
        this.countryCode = countryCode.toUpperCase();
    }

    /**
     * 테스트용 빌더 생성자
     */
    @Builder(builderMethodName = "withId", access = AccessLevel.PRIVATE)
    private Country(Long id, String name, String countryCode) {
        this.id = id;
        this.name = name;
        this.countryCode = countryCode.toUpperCase();
    }

    // code와 name 중 하나라도 같으면 같은 국가로 간주한다
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Country)) return false;
        Country country = (Country) obj;
        return countryCode.equals(country.countryCode) || name.equals(country.name);
    }
}
