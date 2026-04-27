package com.ppopi.ppopihouse.auth.service;

import com.ppopi.ppopihouse.auth.dto.response.KakaoUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class KakaoClient {

    private final WebClient.Builder webClientBuilder;

    public KakaoUserResponse getUserInfo(String accessToken) {
        return webClientBuilder.build()
                .get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(KakaoUserResponse.class)
                .block();
    }
}