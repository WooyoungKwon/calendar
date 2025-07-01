package com.planit.calendar.holiday.domain;

import com.planit.calendar.country.domain.Country;
import com.planit.calendar.util.StringListConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Holiday {

    @Id
    @GeneratedValue
    @Column(name = "holiday_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "local_name", nullable = false)
    private String localName;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "fixed", nullable = false)
    private boolean fixed;

    @Column(name = "global", nullable = false)
    private boolean global;

    @Column(name = "counties")
    @Convert(converter = StringListConverter.class)
    private List<String> counties;

    @Column(name = "launch_year")
    private Integer launchYear;

    @Column(name = "types")
    @Convert(converter = StringListConverter.class)
    private List<String> types;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    @Builder
    public Holiday(String date, String localName, String name, boolean fixed, boolean global, List<String> counties,
        String launchYear, List<String> types, Country country) {
        this.date = LocalDate.parse(date);
        this.localName = localName;
        this.name = name;
        this.fixed = fixed;
        this.global = global;
        this.counties = counties;
        this.launchYear = launchYear != null ? Integer.parseInt(launchYear) : null;
        this.types = types;
        this.country = country;
    }

    /**
     * 테스트용 빌더 생성자
     */
    @Builder(builderMethodName = "testObjWithId")
    public Holiday(Long id, String date, String localName, String name, boolean fixed, boolean global,
        List<String> counties, String launchYear, List<String> types, Country country) {
        this.id = id;
        this.date = LocalDate.parse(date);
        this.localName = localName;
        this.name = name;
        this.fixed = fixed;
        this.global = global;
        this.counties = counties;
        this.launchYear = launchYear != null ? Integer.parseInt(launchYear) : null;
        this.types = types;
        this.country = country;
    }
}
