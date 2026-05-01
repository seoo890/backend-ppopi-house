
package com.ppopi.ppopihouse.diagnosis.service;

import com.ppopi.ppopihouse.diagnosis.dto.external.AiDiagnosisRequest;
import com.ppopi.ppopihouse.diagnosis.dto.external.AiDiagnosisResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class AiDiagnosisClientImpl implements AiDiagnosisClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${external.ai-api.base-url}")
    private String aiApiBaseUrl;

    public AiDiagnosisResponse diagnose(AiDiagnosisRequest request) {
        return webClientBuilder.build()
                .post()
                .uri(aiApiBaseUrl + "/diagnose")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AiDiagnosisResponse.class)
                .block();
    }
}
