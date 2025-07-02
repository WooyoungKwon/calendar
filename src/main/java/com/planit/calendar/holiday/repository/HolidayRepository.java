package com.planit.calendar.holiday.repository;

import com.planit.calendar.country.domain.Country;
import com.planit.calendar.holiday.domain.Holiday;
import com.planit.calendar.holiday.dto.response.HolidayInfoWithCountry;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    Page<Holiday> findByCountry_IdAndDateBetween(Long countryId, LocalDate dateAfter, LocalDate dateBefore, Pageable pageable);

    @Query("""
        SELECT new com.planit.calendar.holiday.dto.response.HolidayInfoWithCountry(
            h.id,
            h.date,
            h.localName,
            h.name,
            h.fixed,
            h.global,
            h.counties,
            h.launchYear,
            h.types,
            h.country.name
            )
        FROM Holiday h
        WHERE YEAR(h.date) = :year
        """)
    Page<HolidayInfoWithCountry> findByYear(int year, Pageable pageable);

    @Query("""
        SELECT h
        FROM Holiday h
        WHERE YEAR(h.date) = :year
        AND h.country.countryCode = :countryCode
        """)
    List<Holiday> findByYearAndCountryCode(String year, String countryCode);

    Page<Holiday> findByCountry_Id(Long countryId, Pageable pageable);

    @Query("""
        SELECT h
        FROM Holiday h
        WHERE h.country.id = :countryId
        AND YEAR(h.date) = :year
        """)
    List<Holiday> findAllByCountryAndDate(Long countryId, String year);

    @Query("""
        SELECT h
        FROM Holiday h
        WHERE h.country.id = :countryId
        AND YEAR(h.date) = :year
        """)
    List<Holiday> findByCountryAndYear(Long countryId, String year);

    void deleteByCountry_Id(Long countryId);

    @Modifying
    @Query("DELETE FROM Holiday h WHERE YEAR(h.date) = :year")
    void deleteByDate(String year);

    @Modifying
    @Query("""
        DELETE FROM Holiday h
        WHERE YEAR(h.date) = :year
        AND h.country.id = :countryId
        """)
    void deleteByCountryAndYear(Long countryId, String year);
}
