package com.planit.calendar.country.repository;

import com.planit.calendar.country.domain.Country;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {

    Optional<Country> findCountryByCode(String code);
}
