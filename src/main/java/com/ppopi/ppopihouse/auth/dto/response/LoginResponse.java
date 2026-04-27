package com.ppopi.ppopihouse.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private Long memberId;
    private Boolean isOnboarding;
}