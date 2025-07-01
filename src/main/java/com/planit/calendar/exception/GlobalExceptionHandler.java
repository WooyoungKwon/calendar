package com.planit.calendar.exception;

import com.planit.calendar.exception.custom.DuplicateException;
import com.planit.calendar.exception.custom.NotFoundException;
import com.planit.calendar.response.ResponseCode;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 시스템에서 발생하는 모든 예외를 처리하는 글로벌 핸들러
 */
@Slf4j
@RestControllerAdvice
@Hidden
public class GlobalExceptionHandler {
    /**
     * Exception 에러가 들어오면 InternalServerError(500) 상태코드와 함께 에러 메시지를 반환한다.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponseDto> handleException(Exception exception, HttpServletRequest request) {
        log.error("예상치 못한 예외 발생. 요청 URI: {}, 예외 메시지: {}", request.getRequestURI(), exception.getMessage());
        ResponseCode responseCode = ResponseCode.INTERNAL_SERVER_ERROR;
        ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto(responseCode, request);
        return ResponseEntity.status(responseCode.getStatus()).body(exceptionResponseDto);
    }

    /**
     * RuntimeException 에러가 들어오면 InternalServerError(500) 상태코드와 함께 에러 메시지를 반환한다.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionResponseDto> handleRuntimeException(RuntimeException exception, HttpServletRequest request) {
        log.error("예상치 못한 런타임 예외 발생. 요청 URI: {}, 예외 메시지: {}", request.getRequestURI(), exception.getMessage());
        ResponseCode responseCode = ResponseCode.INTERNAL_SERVER_ERROR;
        ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto(responseCode, request);
        return ResponseEntity.status(responseCode.getStatus()).body(exceptionResponseDto);
    }

    /**
     * IllegalArgumentException 에러가 들어오면 BadRequest(400) 상태코드와 함께 에러 메시지를 반환한다.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponseDto> handleIllegalArgumentException(IllegalArgumentException exception, HttpServletRequest request) {
        log.warn("잘못된 인자 예외 발생. 요청 URI: {}, 예외 메시지: {}", request.getRequestURI(), exception.getMessage());
        ResponseCode responseCode = ResponseCode.BAD_REQUEST;
        ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto(responseCode, request);
        return ResponseEntity.status(responseCode.getStatus()).body(exceptionResponseDto);
    }

    /**
     * 시스템 상태가 요청을 수행할 수 없는 경우 BadRequest(400) 상태코드와 함께 에러 메시지를 반환한다.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ExceptionResponseDto> handleIllegalStateException(IllegalStateException exception, HttpServletRequest request) {
        log.warn("서버의 상태에 어긋나는 파라미터로 요청. 요청 URI: {}, 예외 메시지: {}", request.getRequestURI(), exception.getMessage());
        ResponseCode responseCode = ResponseCode.BAD_REQUEST;
        ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto(responseCode, request);
        return ResponseEntity.status(responseCode.getStatus()).body(exceptionResponseDto);
    }

    /**
     * 존재하지 않는 리소스를 요청하면 NotFound(404) 상태코드와 함께 에러 메시지를 반환한다.
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponseDto> handleNotFoundException(NotFoundException exception, HttpServletRequest request) {
        log.warn("존재하지 않는 리소스 요청. 요청 URI: {}, 예외 메시지: {}", request.getRequestURI(), exception.getMessage());
        ResponseCode responseCode = ResponseCode.NOT_FOUND;
        ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto(responseCode, request);
        return ResponseEntity.status(responseCode.getStatus()).body(exceptionResponseDto);
    }

    /**
     * 중복이 허용되지 않는 필드에 중복되는 데이터가 들어오면 Conflict(409) 상태코드와 함께 에러 메시지를 반환한다.
     */
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ExceptionResponseDto> handleDuplicateException(DuplicateException exception, HttpServletRequest request) {
        log.warn("중복되는 데이터로 요청. 요청 URI: {}, 예외 메시지: {}", request.getRequestURI(), exception.getMessage());
        ResponseCode responseCode = ResponseCode.CONFLICT;
        ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto(responseCode, request);
        return ResponseEntity.status(responseCode.getStatus()).body(exceptionResponseDto);
    }

    /**
     * 요청 파라미터 값이 형식과 다를 때 BadRequest(400) 상태코드와 함께 에러 메시지를 반환한다.
     * 예를 들어 2025-12-32 같은 값이 들어오는 경우
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionResponseDto> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception, HttpServletRequest request) {
        log.warn("요청 파라미터 형식 오류. 요청 URI: {}, 예외 메시지: {}", request.getRequestURI(), exception.getMessage());
        ResponseCode responseCode = ResponseCode.INVALID_PARAMETER;
        ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto(responseCode, request);
        return ResponseEntity.badRequest().body(exceptionResponseDto);
    }

}
