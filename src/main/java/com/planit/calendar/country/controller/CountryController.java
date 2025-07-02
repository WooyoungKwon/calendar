package com.planit.calendar.country.controller;

import com.planit.calendar.country.dto.ChangedDataCount;
import com.planit.calendar.country.service.CountryService;
import com.planit.calendar.response.ResponseCode;
import com.planit.calendar.response.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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


}
