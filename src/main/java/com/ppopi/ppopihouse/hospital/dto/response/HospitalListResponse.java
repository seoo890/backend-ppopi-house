package com.ppopi.ppopihouse.hospital.dto.response;

import com.ppopi.ppopihouse.hospital.external.kakao.KakaoPlaceResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import com.ppopi.ppopihouse.hospital.external.google.GooglePlaceResponse;

@Getter
@AllArgsConstructor
public class HospitalListResponse {

    private String hospitalId;
    private String name;
    private String address;
    private String callNumber;
    private String businessHours;
    private String operationLabel;
    private boolean is24hr;
    private long distanceMeter;

    public static HospitalListResponse from(
            KakaoPlaceResponse.Document kakaoPlace,
            GooglePlaceResponse.GooglePlace googlePlace,
            String businessHours,
            String operationLabel,
            boolean is24hr,
            long distanceMeter
    ) {
        String name = kakaoPlace.place_name();

        String address = kakaoPlace.road_address_name() != null && !kakaoPlace.road_address_name().isBlank()
                ? kakaoPlace.road_address_name()
                : kakaoPlace.address_name();

        if ((address == null || address.isBlank()) && googlePlace != null) {
            address = googlePlace.formattedAddress();
        }

        String callNumber = kakaoPlace.phone() != null && !kakaoPlace.phone().isBlank()
                ? kakaoPlace.phone()
                : null;

        if ((callNumber == null || callNumber.isBlank()) && googlePlace != null) {
            callNumber = googlePlace.nationalPhoneNumber() != null
                    ? googlePlace.nationalPhoneNumber()
                    : googlePlace.internationalPhoneNumber();
        }

        return new HospitalListResponse(
                kakaoPlace.id(),
                name != null ? name : "이름 없음",
                address != null && !address.isBlank() ? address : "주소 정보 없음",
                callNumber != null && !callNumber.isBlank() ? callNumber : "전화번호 정보 없음",
                businessHours != null ? businessHours : "10:00 - 20:00",
                operationLabel != null ? operationLabel : "정보 없음",
                is24hr,
                distanceMeter
        );
    }
}