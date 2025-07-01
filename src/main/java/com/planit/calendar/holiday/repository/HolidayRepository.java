package com.planit.calendar.holiday.repository;

import com.planit.calendar.holiday.domain.Holiday;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    Page<Holiday> findByCountry_IdAndDateBetween(Long countryId, LocalDate dateAfter, LocalDate dateBefore, Pageable pageable);
}
