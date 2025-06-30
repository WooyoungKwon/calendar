package com.planit.calendar.holiday.repository;

import com.planit.calendar.holiday.domain.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {

}
