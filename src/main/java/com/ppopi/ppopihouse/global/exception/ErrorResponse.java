package com.ppopi.ppopihouse.global.exception;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
public class ErrorResponse {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    private final String timestamp;
    private final String code;
    private final String message;

    public ErrorResponse(String code, String message) {
        this.timestamp = OffsetDateTime.now(SEOUL_ZONE)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        this.code = code;
        this.message = message;
    }
}