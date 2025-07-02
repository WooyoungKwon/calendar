package com.planit.calendar.country.controller;

import com.planit.calendar.country.dto.request.CountryByYearRequest;
import com.planit.calendar.country.dto.response.ChangedDataCount;
import com.planit.calendar.country.dto.response.CountryInfoDto;
import com.planit.calendar.country.dto.response.CountryListDto;
import com.planit.calendar.country.dto.request.CountryPageableDto;
import com.planit.calendar.country.service.CountryService;
import com.planit.calendar.holiday.dto.request.HolidayPageableDto;
import com.planit.calendar.holiday.dto.request.HolidaySearchByCountryRequest;
import com.planit.calendar.holiday.dto.response.HolidaySearchResponse;
import com.planit.calendar.holiday.service.HolidayService;
import com.planit.calendar.response.ResponseCode;
import com.planit.calendar.response.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/country")
public class CountryController {

    private final HolidayService holidayService;
    private final CountryService countryService;

    @GetMapping("/list")
    @Operation(summary = "국가 목록 조회", description = "저장된 국가 목록을 조회합니다.")
    public ResponseEntity<ResponseDto<CountryListDto>> getCountryList(
        @ModelAttribute @Valid CountryPageableDto countryPageableDto
    ) {
        CountryListDto countryListDto = holidayService.getAllCountries(countryPageableDto);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDto.success(ResponseCode.COUNTRY_LIST_SUCCESS, countryListDto));
    }

    @GetMapping("/code")
    @Operation(summary = "국가 정보 조회", description = "국가 코드로 국가 정보를 조회합니다.")
    public ResponseEntity<ResponseDto<?>> getCountryByCode(
        @RequestParam String countryCode
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDto.success(ResponseCode.COUNTRY_FIND_SUCCESS, countryService.getCountryByCode(countryCode.toUpperCase())));
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

    @DeleteMapping("/holidayAndYear")
    @Operation(summary = "해당 국가의 해당 연도 공휴일 데이터 삭제", description = "해당 국가의 해당 연도 데이터를 전부 삭제합니다.")
    public ResponseEntity<ResponseDto<?>> deleteAllCountryHolidaysAndYearHolidays(
        @ModelAttribute @Valid CountryByYearRequest request
    ) {
        holidayService.deleteAllByCountryAndYear(request.getCountryId(), request.getYear());

        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.success(ResponseCode.HOLIDAY_DELETE_SUCCESS, null));
    }

    @DeleteMapping("/holiday")
    @Operation(summary = "국가별 공휴일 데이터 삭제", description = "국가별 공휴일 데이터를 전부 삭제합니다.")
    public ResponseEntity<ResponseDto<?>> deleteAllCountryHolidays(
        @RequestParam Long countryId
    ) {
        holidayService.deleteAllByCountry(countryId);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.success(ResponseCode.HOLIDAY_DELETE_SUCCESS, null));
    }

    @PostMapping("/synchronize/country")
    @Operation(summary = "국가별 공휴일 데이터 동기화", description = "국가 ID를 기준으로 외부 API에서 공휴일 데이터를 조회하고, 기존에 저장 돼 있던 해당 국가 데이터에 덮어씌웁니다.")
    public ResponseEntity<ResponseDto<ChangedDataCount>> synchronizeCountry(
        @RequestParam Long countryId
    ) {
        ChangedDataCount changedDataCount = holidayService.synchronizeByCountry(countryId);
        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDto.success(ResponseCode.HOLIDAY_SYNCHRONIZE_SUCCESS, changedDataCount));
    }


}
