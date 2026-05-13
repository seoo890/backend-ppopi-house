package com.ppopi.ppopihouse.auth.controller;

import com.ppopi.ppopihouse.auth.dto.request.KakaoLoginRequest;
import com.ppopi.ppopihouse.auth.dto.request.RefreshTokenRequest;
import com.ppopi.ppopihouse.auth.dto.response.LoginResponse;
import com.ppopi.ppopihouse.auth.dto.response.TokenResponse;
import com.ppopi.ppopihouse.auth.security.CustomUserDetails;
import com.ppopi.ppopihouse.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public TokenResponse reissue(
            @RequestBody RefreshTokenRequest request
    ) {
        return authService.reissue(request.getRefreshToken());
    }

    @Operation(
            summary = "로그아웃",
            description = """
                현재 로그인한 회원의 refreshToken을 삭제하여 로그아웃 처리합니다.
                
                accessToken은 stateless 방식이므로 서버에서 직접 만료시키지 않고,
                Redis에 저장된 refreshToken을 삭제하여 토큰 재발급을 차단합니다.
                """
    )
    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        authService.logout(userDetails.getMemberId());

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "회원탈퇴",
            description = """
                현재 로그인한 회원을 탈퇴 처리합니다.
                
                회원 데이터는 즉시 물리 삭제하지 않고 soft delete 방식으로 처리합니다.
                탈퇴 시 Redis에 저장된 refreshToken을 삭제하여 재로그인 및 토큰 재발급을 차단합니다.
                """
    )
    @DeleteMapping("/withdraw")
    public ResponseEntity<Void> withdraw(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        authService.withdraw(userDetails.getMemberId());
        return ResponseEntity.ok().build();
    }
}