package com.planit.calendar.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {
    SUCCESS(200, "2000", "요청이 성공적으로 처리되었습니다."),
    CREATED(201, "2001", "리소스를 성공적으로 생성했습니다."),

    // 국가 관련 코드
    COUNTRY_NOT_FOUND(404, "4011", "해당 국가를 찾을 수 없습니다."),
    COUNTRY_FIND_SUCCESS(200, "2010", "국가 정보 조회 성공"),
    COUNTRY_LIST_SUCCESS(200, "2013", "국가 목록 조회 성공"),

    // 공휴일 관련 코드
    HOLIDAY_SEARCH_SUCCESS(200, "2010", "공휴일 조회 성공"),
    HOLIDAY_NOT_FOUND(404, "4010", "해당 조건에 맞는 공휴일을 찾을 수 없습니다."),
    HOLIDAY_SAVE_SUCCESS(200, "2011", "공휴일 저장 성공"),
    HOLIDAY_SYNCHRONIZE_SUCCESS(200, "2012", "공휴일 데이터 동기화 성공"),
    HOLIDAY_DELETE_SUCCESS(200, "2015", "공휴일 데이터가 삭제되었습니다."),

    // 유효성 검사 관련 코드
    INVALID_PARAMETER(400, "4001", "요청 파라미터가 유효하지 않습니다."),
    INVALID_DATE_FORMAT(400, "4002", "날짜 형식이 올바르지 않습니다."),
    YEAR_RANGE_INVALID(400, "4003", "'시작 연도'는 '종료 연도'보다 클 수 없습니다."),

    // 일반 에러 코드
    BAD_REQUEST(400, "4000", "잘못된 요청입니다."),
    UNAUTHORIZED(401, "4004", "인증 정보가 유효하지 않습니다."),
    FORBIDDEN(403, "4005", "접근 권한이 없습니다."),
    NOT_FOUND(404, "4006", "요청한 리소스를 찾을 수 없습니다."),
    CONFLICT(409, "4007", "데이터 충돌이 발생했습니다."),
    INTERNAL_SERVER_ERROR(500, "5000", "서버 내부 오류가 발생했습니다.");


    private final int status;       // HTTP 상태 코드
    private final String code;      // 내부적으로 사용하는 커스텀 코드 (선택 사항)
    private final String message;   // 클라이언트에게 보여줄 메시지

}
