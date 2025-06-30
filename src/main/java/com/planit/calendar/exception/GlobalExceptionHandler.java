package com.planit.calendar.exception;

import com.planit.calendar.exception.custom.DuplicateException;
import com.planit.calendar.exception.custom.NotFoundException;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 시스템에서 발생하는 모든 예외를 처리하는 글로벌 핸들러
 */
@RestControllerAdvice
@Hidden
public class GlobalExceptionHandler {
    /**
     * Exception 에러가 들어오면 BadRequest(400) 상태코드와 함께 에러 메시지를 반환한다.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponseDto> handleException(Exception exception, HttpServletRequest request) {
        ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto(
            HttpStatus.BAD_REQUEST,
            exception.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponseDto);
    }

    /**
     * RuntimeException 에러가 들어오면 InternalServerError(500) 상태코드와 함께 에러 메시지를 반환한다.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionResponseDto> handleRuntimeException(RuntimeException exception, HttpServletRequest request) {
        ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto(
            HttpStatus.INTERNAL_SERVER_ERROR,
            exception.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseDto);
    }

    /**
     * IllegalArgumentException 에러가 들어오면 BadRequest(400) 상태코드와 함께 에러 메시지를 반환한다.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponseDto> handleIllegalArgumentException(IllegalArgumentException exception, HttpServletRequest request) {
        ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto(
            HttpStatus.BAD_REQUEST,
            exception.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(exceptionResponseDto);
    }

    /**
     * 시스템 상태가 요청을 수행할 수 없는 경우 Forbidden(403) 상태코드와 함께 에러 메시지를 반환한다.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ExceptionResponseDto> handleIllegalStateException(IllegalStateException exception, HttpServletRequest request) {
        ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto(
            HttpStatus.FORBIDDEN,
            exception.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(exceptionResponseDto);
    }

    /**
     * 존재하지 않는 리소스를 요청하면 NotFound(404) 상태코드와 함께 에러 메시지를 반환한다.
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponseDto> handleNotFoundException(NotFoundException exception, HttpServletRequest request) {
        ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto(
            HttpStatus.NOT_FOUND,
            exception.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponseDto);
    }

    /**
     * 중복이 허용되지 않는 필드에 중복되는 데이터가 들어오면 Conflict(409) 상태코드와 함께 에러 메시지를 반환한다.
     */
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ExceptionResponseDto> handleDuplicateException(DuplicateException exception, HttpServletRequest request) {
        ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto(
            HttpStatus.CONFLICT,
            exception.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exceptionResponseDto);
    }

}
