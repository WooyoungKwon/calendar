package com.planit.calendar.holiday.controller;

import com.planit.calendar.country.dto.ChangedDataCount;
import com.planit.calendar.holiday.dto.request.HolidayPageableDto;
import com.planit.calendar.holiday.dto.request.HolidaySearchByCountryRequest;
import com.planit.calendar.holiday.dto.request.HolidaySearchByYearRequest;
import com.planit.calendar.holiday.dto.request.HolidaySearchRequest;
import com.planit.calendar.holiday.dto.response.HolidaySearchResponse;
import com.planit.calendar.holiday.service.HolidayService;
import com.planit.calendar.response.ResponseCode;
import com.planit.calendar.response.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/holiday")
public class HolidayController {

    private final HolidayService holidayService;

    @GetMapping("")
    @Operation(summary = "나라와 연도 조건으로 공휴일 페이징", description = "파라미터로 받은 나라와 연도 시작, 연도 끝 사이에 있는 공휴일 데이터를 페이징하여 조회합니다.")
    public ResponseEntity<ResponseDto<HolidaySearchResponse>> getHolidays(
        @ModelAttribute @Valid HolidaySearchRequest holidaySearchRequest,
        @ModelAttribute @Valid HolidayPageableDto holidayPageableDto
    ) {
        HolidaySearchResponse holidaysByConditions = holidayService.getHolidaysByConditions(
            holidayPageableDto, holidaySearchRequest);
        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDto.success(ResponseCode.HOLIDAY_SEARCH_SUCCESS, holidaysByConditions));
    }

    @GetMapping("/year")
    @Operation(summary = "연도별 공휴일 조회", description = "파라미터로 받은 연도의 공휴일 데이터를 페이징하여 조회합니다.")
    public ResponseEntity<ResponseDto<HolidaySearchResponse>> getHolidaysByYear(
        @ModelAttribute @Valid HolidaySearchByYearRequest request,
        @ModelAttribute @Valid HolidayPageableDto holidayPageableDto
    ) {
        HolidaySearchResponse holidaysByConditions = holidayService.getHolidaysByYear(holidayPageableDto, request);
        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDto.success(ResponseCode.HOLIDAY_SEARCH_SUCCESS, holidaysByConditions));
    }

    @GetMapping("/country")
    @Operation(summary = "국가별 공휴일 조회", description = "파라미터로 받은 국가의 공휴일 데이터를 페이징하여 조회합니다.")
    public ResponseEntity<ResponseDto<HolidaySearchResponse>> getHolidaysByCountry(
        @ModelAttribute @Valid HolidaySearchByCountryRequest request,
        @ModelAttribute @Valid HolidayPageableDto holidayPageableDto
    ) {
        HolidaySearchResponse holidaysByConditions = holidayService.getHolidaysByCountry(holidayPageableDto, request);
        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDto.success(ResponseCode.HOLIDAY_SEARCH_SUCCESS, holidaysByConditions));
    }

    @PostMapping("/synchronize/country")
    @Operation(summary = "국가별 공휴일 데이터 동기화", description = "국가 ID를 기준으로 외부 API에서 공휴일 데이터를 조회하고, 기존에 저장 돼 있던 해당 국가 데이터에 덮어씌웁니다.")
    public ResponseEntity<ResponseDto<ChangedDataCount>> synchronizeCountry(
        @RequestParam Long countryId
    ) {
        ChangedDataCount changedDataCount = holidayService.synchronizeByCountry(countryId);
        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDto.success(ResponseCode.COUNTRY_HOLIDAY_SYNCHRONIZE_SUCCESS, changedDataCount));
    }

    @PostMapping("/synchronize/year")
    @Operation(summary = "연도별 공휴일 데이터 동기화", description = "연도를 기준으로 외부 API에서 공휴일 데이터를 조회하고, 기존에 저장 돼 있던 연도 데이터에 덮어씌웁니다.")
    public ResponseEntity<ResponseDto<ChangedDataCount>> synchronizeYear(
        @RequestParam String year
    ) {
        ChangedDataCount changedDataCount = holidayService.synchronizeByYear(year);
        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDto.success(ResponseCode.COUNTRY_HOLIDAY_SYNCHRONIZE_SUCCESS, changedDataCount));
    }
}
