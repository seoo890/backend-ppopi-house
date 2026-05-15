package com.ppopi.ppopihouse.hospital.external.google;

import com.ppopi.ppopihouse.global.exception.ExternalApiException;
import com.ppopi.ppopihouse.hospital.config.GooglePlacesProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GooglePlacesClient {

    private static final String NEARBY_FIELD_MASK = String.join(",",
            "places.id",
            "places.displayName",
            "places.formattedAddress",
            "places.nationalPhoneNumber",
            "places.internationalPhoneNumber",
            "places.location",
            "places.regularOpeningHours"
    );

    private static final String DETAIL_FIELD_MASK = String.join(",",
            "id",
            "displayName",
            "formattedAddress",
            "nationalPhoneNumber",
            "internationalPhoneNumber",
            "location",
            "regularOpeningHours.openNow",
            "regularOpeningHours.weekdayDescriptions",
            "regularOpeningHours.periods"
    );

    private final GooglePlacesProperties properties;

    public GooglePlaceResponse.GooglePlace searchPlaceForOpeningHours(
            String keyword,
            double latitude,
            double longitude
    ) {
        try {
            GoogleNearbySearchRequest request = new GoogleNearbySearchRequest(
                    List.of("veterinary_care"),
                    5,
                    new GoogleNearbySearchRequest.LocationRestriction(
                            new GoogleNearbySearchRequest.Circle(
                                    new GoogleNearbySearchRequest.Center(latitude, longitude),
                                    500
                            )
                    )
            );

            GooglePlaceResponse response = restClient()
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/places:searchNearby")
                            .queryParam("languageCode", "ko")
                            .queryParam("regionCode", "KR")
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Goog-Api-Key", properties.getApiKey())
                    .header("X-Goog-FieldMask", NEARBY_FIELD_MASK)
                    .body(request)
                    .retrieve()
                    .body(GooglePlaceResponse.class);

            if (response == null || response.places() == null || response.places().isEmpty()) {
                return null;
            }

            return response.places().stream()
                    .filter(place -> isSamePlace(keyword, place.displayName().text()))
                    .findFirst()
                    .orElse(response.places().get(0));

        } catch (Exception e) {
            return null;
        }
    }
    private boolean isSamePlace(String kakaoName, String googleName) {
        if (kakaoName == null || googleName == null) {
            return false;
        }

        String k = normalizePlaceName(kakaoName);
        String g = normalizePlaceName(googleName);

        return k.contains(g) || g.contains(k);
    }

    private String normalizePlaceName(String name) {
        return name.toLowerCase()
                .replace("동물병원", "")
                .replace("종합동물병원", "")
                .replace("동물의료센터", "")
                .replace("animal", "")
                .replace("hospital", "")
                .replaceAll("\\s+", "")
                .trim();
    }

    public GooglePlaceResponse.GooglePlace getPlaceDetail(String placeId) {
        GooglePlaceResponse.GooglePlace response = restClient()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/places/{placeId}")
                        .queryParam("languageCode", "ko")
                        .queryParam("regionCode", "KR")
                        .build(placeId))
                .header("X-Goog-Api-Key", properties.getApiKey())
                .header("X-Goog-FieldMask", DETAIL_FIELD_MASK)
                .retrieve()
                .body(GooglePlaceResponse.GooglePlace.class);

        if (response == null) {
            throw new ExternalApiException("병원 상세 정보를 조회할 수 없습니다.");
        }

        return response;
    }

    private RestClient restClient() {
        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .build();
    }
}