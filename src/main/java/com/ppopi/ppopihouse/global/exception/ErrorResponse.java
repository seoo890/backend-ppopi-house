package com.ppopi.ppopihouse.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private final OffsetDateTime timestamp;

    private String code;
    private String message;

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = OffsetDateTime.now(ZoneId.of("Asia/Seoul"));
    }
}