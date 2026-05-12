package com.ppopi.ppopihouse.diagnosis.service;

import com.ppopi.ppopihouse.diagnosis.dto.external.AiDiagnosisRequest;
import com.ppopi.ppopihouse.diagnosis.dto.external.AiDiagnosisResponse;
import com.ppopi.ppopihouse.global.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

@Component
@RequiredArgsConstructor
public class AiDiagnosisClientImpl implements AiDiagnosisClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${external.ai-api.base-url}")
    private String aiApiBaseUrl;

    public AiDiagnosisResponse diagnose(AiDiagnosisRequest request) {
        try {
            AiDiagnosisResponse response = webClientBuilder.build()
                    .post()
                    .uri(aiApiBaseUrl + "/diagnose")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(AiDiagnosisResponse.class)
                    .block();

            if (response == null) {
                throw new ExternalApiException("AI 진단 서버 응답이 비어 있습니다.");
            }

            return response;

        } catch (WebClientException e) {
            throw new ExternalApiException("AI 진단 서버 호출 중 오류가 발생했습니다.");
        }
    }
}