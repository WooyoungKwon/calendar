package com.planit.calendar.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public class ExceptionResponseDto {
    private HttpStatus statusCode;
    private String errorMessage;
    private String Path;
}
