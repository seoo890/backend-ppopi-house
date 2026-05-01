package com.ppopi.ppopihouse.diagnosis.service;

import com.ppopi.ppopihouse.diagnosis.dto.external.ImageValidationResponse;
import com.ppopi.ppopihouse.diagnosis.service.ImageValidationClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ImageValidationClientImpl implements ImageValidationClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${external.validation-api.base-url}")
    private String validationApiBaseUrl;

    public ImageValidationResponse validate(MultipartFile image) {
        try {
            ByteArrayResource imageResource = new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename();
                }
            };

            LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", imageResource);

            return webClientBuilder.build()
                    .post()
                    .uri(validationApiBaseUrl + "/validate/eye-image")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(body))
                    .retrieve()
                    .bodyToMono(ImageValidationResponse.class)
                    .block();

        } catch (IOException e) {
            throw new RuntimeException("이미지 파일을 읽는 중 오류가 발생했습니다.");
        }
    }
}