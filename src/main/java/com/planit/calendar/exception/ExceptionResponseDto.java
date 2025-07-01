package com.planit.calendar.exception;

import com.planit.calendar.response.ResponseCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ExceptionResponseDto {
    private HttpStatus status; // HTTP 상태 코드
    private String code; // 내부 정의 코드
    private String errorMessage;
    private String path; // 요청된 경로

    public ExceptionResponseDto(ResponseCode responseCode, HttpServletRequest request) {
        this.status = HttpStatus.valueOf(responseCode.getStatus());
        this.code = responseCode.getCode();
        this.errorMessage = responseCode.getMessage();
        this.path = request.getRequestURI();
    }
}
