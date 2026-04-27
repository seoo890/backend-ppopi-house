package com.ppopi.ppopihouse.auth.service;

import com.ppopi.ppopihouse.auth.dto.response.KakaoUserResponse;
import com.ppopi.ppopihouse.auth.dto.response.LoginResponse;
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

    public LoginResponse kakaoLogin(String accessToken) {
        KakaoUserResponse kakaoUser = kakaoClient.getUserInfo(accessToken);

        String kakaoUserId = String.valueOf(kakaoUser.getId());

        Member member = memberRepository.findByKakaoUserId(kakaoUserId)
                .orElseGet(() -> {
                    Member newMember = new Member();
                    newMember.setKakaoUserId(kakaoUserId);
                    return memberRepository.save(newMember);
                });

        boolean hasPet = petRepository.existsByMember(member);
        boolean isOnboarding = !hasPet;

        return new LoginResponse(member.getMemberId(), isOnboarding);
    }
}