package com.ppopi.ppopihouse.auth.controller;

import com.ppopi.ppopihouse.auth.dto.request.KakaoLoginRequest;
import com.ppopi.ppopihouse.auth.dto.response.LoginResponse;
import com.ppopi.ppopihouse.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/kakao")
    public LoginResponse kakaoLogin(@RequestBody KakaoLoginRequest request) {
        return authService.kakaoLogin(request.getAccessToken());
    }
}