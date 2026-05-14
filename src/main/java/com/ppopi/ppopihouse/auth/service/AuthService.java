package com.ppopi.ppopihouse.auth.service;

import com.ppopi.ppopihouse.auth.config.JwtProperties;
import com.ppopi.ppopihouse.auth.dto.response.KakaoUserResponse;
import com.ppopi.ppopihouse.auth.dto.response.LoginResponse;
import com.ppopi.ppopihouse.auth.dto.response.TokenResponse;
import com.ppopi.ppopihouse.auth.repository.RefreshTokenRepository;
import com.ppopi.ppopihouse.global.exception.UnauthorizedException;
import com.ppopi.ppopihouse.member.domain.Member;
import com.ppopi.ppopihouse.member.repository.MemberRepository;
import com.ppopi.ppopihouse.pet.domain.Pet;
import com.ppopi.ppopihouse.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

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
                .map(existingMember -> {
                    if (existingMember.isDeleted()) {
                        existingMember.rejoin();
                    }
                    return existingMember;
                })
                .orElseGet(() -> createMember(kakaoUserId));

        boolean hasPet = petRepository.existsByMemberAndDeletedFalse(member);
        boolean isOnboarding = !hasPet;

        String accessToken = jwtTokenProvider.createAccessToken(member.getMemberId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getMemberId());

        saveRefreshToken(member.getMemberId(), refreshToken);

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

        Member member = findMember(memberId);
        validateActiveMemberForReissue(member);

        String savedToken = refreshTokenRepository.find(memberId);

        if (savedToken == null || !savedToken.equals(refreshToken)) {
            throw new UnauthorizedException("refresh token이 일치하지 않습니다.");
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(memberId);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(memberId);

        saveRefreshToken(memberId, newRefreshToken);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    public void logout(Long memberId) {
        System.out.println("logout memberId = " + memberId);

        refreshTokenRepository.delete(memberId);

        String savedToken = refreshTokenRepository.find(memberId);
        System.out.println("logout 후 refreshToken = " + savedToken);
    }

    @Transactional
    public void withdraw(Long memberId) {
        Member member = findMember(memberId);

        refreshTokenRepository.delete(memberId);

        if (member.isDeleted()) {
            return;
        }

        List<Pet> pets = petRepository.findAllByMemberAndDeletedFalseOrderByPetIdAsc(member);
        LocalDateTime now = LocalDateTime.now(SEOUL_ZONE);

        for (Pet pet : pets) {
            pet.delete(now);
        }

        member.withdraw(now);
    }

    private Member createMember(String kakaoUserId) {
        Member member = new Member();
        member.setKakaoUserId(kakaoUserId);
        return memberRepository.save(member);
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
    }

    private void validateActiveMember(Member member) {
        if (member.isDeleted()) {
            throw new UnauthorizedException("탈퇴한 회원입니다.");
        }
    }

    private void validateActiveMemberForReissue(Member member) {
        if (member.isDeleted()) {
            refreshTokenRepository.delete(member.getMemberId());
            throw new UnauthorizedException("탈퇴한 회원입니다.");
        }
    }

    private void saveRefreshToken(Long memberId, String refreshToken) {
        refreshTokenRepository.save(
                memberId,
                refreshToken,
                jwtProperties.getRefreshExpiration()
        );
    }
}