package com.ppopi.ppopihouse.hospital.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ppopi.ppopihouse.hospital.external.kakao.KakaoPlaceResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HospitalListResponse {

    private String hospitalId;
    private String name;
    private double latitude;
    private double longitude;
    private long distanceMeter;

    @JsonProperty("is24hr")
    private boolean is24hr;

    public static HospitalListResponse from(
            KakaoPlaceResponse.Document kakaoPlace,
            boolean is24hr,
            long distanceMeter
    ) {
        double latitude = Double.parseDouble(kakaoPlace.y());
        double longitude = Double.parseDouble(kakaoPlace.x());

        return new HospitalListResponse(
                kakaoPlace.id(),
                kakaoPlace.place_name() != null ? kakaoPlace.place_name() : "이름 없음",
                latitude,
                longitude,
                distanceMeter,
                is24hr
        );
    }
}