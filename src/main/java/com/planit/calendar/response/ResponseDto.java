package com.planit.calendar.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseDto<T> {
    private int status;
    private String code;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public static <T> ResponseDto<T> success(ResponseCode responseCode, T data) {
        return ResponseDto.<T>builder()
            .status(responseCode.getStatus())
            .code(responseCode.getCode())
            .message(responseCode.getMessage())
            .data(data)
            .timestamp(LocalDateTime.now()) // 빌더에서 timestamp 초기화
            .build();
    }
}
