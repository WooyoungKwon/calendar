package com.planit.calendar.country.repository;

import com.planit.calendar.country.domain.Country;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    @Query("SELECT c.countryCode FROM Country c")
    List<String> getAllCountryCode();

    Optional<Country> findByCountryCode(String countryCode);
}
