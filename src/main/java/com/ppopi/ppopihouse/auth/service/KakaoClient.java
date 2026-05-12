package com.ppopi.ppopihouse.auth.service;

import com.ppopi.ppopihouse.auth.dto.response.KakaoUserResponse;
import com.ppopi.ppopihouse.global.exception.ExternalApiException;
import com.ppopi.ppopihouse.global.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class KakaoClient {

    private final WebClient.Builder webClientBuilder;

    public KakaoUserResponse getUserInfo(String accessToken) {

        try {
            KakaoUserResponse response = webClientBuilder.build()
                    .get()
                    .uri("https://kapi.kakao.com/v2/user/me")
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .retrieve()

                    // 카카오 서버가 4xx/5xx 반환 시 처리
                    .onStatus(
                            HttpStatusCode::is4xxClientError,
                            clientResponse -> {
                                throw new UnauthorizedException("유효하지 않은 카카오 액세스 토큰입니다.");
                            }
                    )

                    .onStatus(
                            HttpStatusCode::is5xxServerError,
                            clientResponse -> {
                                throw new ExternalApiException("카카오 서버 내부 오류가 발생했습니다.");
                            }
                    )

                    .bodyToMono(KakaoUserResponse.class)
                    .block();

            if (response == null) {
                throw new ExternalApiException("카카오 사용자 정보 응답이 비어 있습니다.");
            }

            return response;

        } catch (WebClientResponseException e) {

            // 혹시 onStatus 못 탄 경우 대비
            if (e.getStatusCode().is4xxClientError()) {
                throw new UnauthorizedException("유효하지 않은 카카오 액세스 토큰입니다.");
            }

            throw new ExternalApiException("카카오 API 호출 중 오류가 발생했습니다.");
        }
    }
}