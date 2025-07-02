package com.planit.calendar.country.controller;

import com.planit.calendar.country.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/country")
public class CountryController {

    private final CountryService countryService;

    @PostMapping("/synchronize")
    public ResponseEntity<?> synchronizeCountry(
        @RequestParam(required = false) Long countryId
    ) {
        countryService.synchronizeCountry(countryId);

        return ResponseEntity.ok().build();
    }
}
