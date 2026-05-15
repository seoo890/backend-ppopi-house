package com.ppopi.ppopihouse.hospital.external.kakao;

import com.ppopi.ppopihouse.hospital.config.KakaoProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KakaoLocalClient {

    private final KakaoProperties properties;

    public List<KakaoPlaceResponse.Document> searchAnimalHospitals(
            double latitude,
            double longitude,
            int limit
    ) {
        try {
            KakaoPlaceResponse response = restClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v2/local/search/keyword.json")
                            .queryParam("query", "동물병원")
                            .queryParam("x", longitude)
                            .queryParam("y", latitude)
                            .queryParam("radius", 5000)
                            .queryParam("size", limit)
                            .queryParam("sort", "distance")
                            .build())
                    .header("Authorization", "KakaoAK " + properties.getRestApiKey())
                    .retrieve()
                    .body(KakaoPlaceResponse.class);

            if (response == null || response.documents() == null) {
                return List.of();
            }

            return response.documents();

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    private String normalizeName(String name) {
        return removeHtml(name)
                .toLowerCase()
                .replace("동물병원", "")
                .replace("종합동물병원", "")
                .replace("동물의료센터", "")
                .replace("animal", "")
                .replace("hospital", "")
                .replaceAll("\\s+", "")
                .trim();
    }

    public String removeHtml(String text) {
        if (text == null) {
            return null;
        }

        return text.replaceAll("<[^>]*>", "");
    }

    private RestClient restClient() {
        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .build();
    }
}