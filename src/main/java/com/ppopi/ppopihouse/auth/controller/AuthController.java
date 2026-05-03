package com.ppopi.ppopihouse.auth.controller;

import com.ppopi.ppopihouse.auth.dto.request.KakaoLoginRequest;
import com.ppopi.ppopihouse.auth.dto.request.RefreshTokenRequest;
import com.ppopi.ppopihouse.auth.dto.response.LoginResponse;
import com.ppopi.ppopihouse.auth.dto.response.TokenResponse;
import com.ppopi.ppopihouse.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "카카오 로그인",
            description = """
                    카카오에서 발급받은 accessToken으로 로그인을 진행합니다.
                    
                    로그인 성공 시 서비스 자체 accessToken, refreshToken을 발급합니다.
                    신규 회원인 경우 온보딩 여부를 함께 반환하여 클라이언트가 반려동물 등록 화면으로 이동할 수 있도록 합니다.
                    """
    )
    @PostMapping("/kakao")
    public LoginResponse kakaoLogin(@RequestBody KakaoLoginRequest request) {
        return authService.kakaoLogin(request.getAccessToken());
    }

    @Operation(
            summary = "토큰 재발급",
            description = """
                    refreshToken을 사용하여 새로운 accessToken과 refreshToken을 재발급합니다.
                    
                    accessToken이 만료된 경우 클라이언트는 이 API를 호출하여 인증 상태를 갱신합니다.
                    """
    )
    @PostMapping("/reissue")
    public TokenResponse reissue(@RequestBody RefreshTokenRequest request) {
        return authService.reissue(request.getRefreshToken());
    }
}