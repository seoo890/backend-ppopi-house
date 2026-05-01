package com.ppopi.ppopihouse.diagnosis.dto.external;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ImageValidationResponse {

    private boolean valid;
    private String stage;
    private String reasonCode;
    private String message;
}