package com.ppopi.ppopihouse.auth.service;

import com.ppopi.ppopihouse.auth.config.JwtProperties;
import com.ppopi.ppopihouse.auth.dto.response.KakaoUserResponse;
import com.ppopi.ppopihouse.auth.dto.response.LoginResponse;
import com.ppopi.ppopihouse.auth.dto.response.TokenResponse;
import com.ppopi.ppopihouse.auth.repository.RefreshTokenRepository;
import com.ppopi.ppopihouse.global.exception.UnauthorizedException;
import com.ppopi.ppopihouse.member.domain.Member;
import com.ppopi.ppopihouse.member.repository.MemberRepository;
import com.ppopi.ppopihouse.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoClient kakaoClient;
    private final MemberRepository memberRepository;
    private final PetRepository petRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    public LoginResponse kakaoLogin(String kakaoAccessToken) {
        KakaoUserResponse kakaoUser = kakaoClient.getUserInfo(kakaoAccessToken);

        String kakaoUserId = String.valueOf(kakaoUser.getId());

        Member member = memberRepository.findByKakaoUserId(kakaoUserId)
                .orElseGet(() -> {
                    Member newMember = new Member();
                    newMember.setKakaoUserId(kakaoUserId);
                    return memberRepository.save(newMember);
                });

        boolean hasPet = petRepository.existsByMember(member);
        boolean isOnboarding = !hasPet;

        // ✅ 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(member.getMemberId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getMemberId());

        // ✅ Redis 저장
        refreshTokenRepository.save(
                member.getMemberId(),
                refreshToken,
                jwtProperties.getRefreshExpiration()
        );

        return new LoginResponse(
                member.getMemberId(),
                isOnboarding,
                accessToken,
                refreshToken
        );
    }

    public TokenResponse reissue(String refreshToken) {

        jwtTokenProvider.validateOrThrow(refreshToken);

        Long memberId = jwtTokenProvider.getMemberId(refreshToken);

        String savedToken = refreshTokenRepository.find(memberId);

        if (savedToken == null || !savedToken.equals(refreshToken)) {
            throw new UnauthorizedException("refresh token이 일치하지 않습니다.");
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(memberId);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(memberId);

        refreshTokenRepository.save(
                memberId,
                newRefreshToken,
                jwtProperties.getRefreshExpiration()
        );

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    public void logout(Long memberId) {
        refreshTokenRepository.delete(memberId);
    }

}