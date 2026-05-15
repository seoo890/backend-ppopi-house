package com.ppopi.ppopihouse.hospital.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.ppopi.ppopihouse.hospital.external.google.GooglePlaceResponse;

@Getter
@AllArgsConstructor
public class HospitalDetailResponse {

    private String hospitalId;
    private String name;
    private String address;
    private String callNumber;
    private String businessHours;
    private String operationLabel;
    private boolean is24hr;
    private long distanceMeter;

    public static HospitalDetailResponse from(
            GooglePlaceResponse.GooglePlace place,
            long distanceMeter,
            String businessHours,
            String operationLabel,
            boolean is24hr
    ) {
        String callNumber = place.nationalPhoneNumber() != null
                ? place.nationalPhoneNumber()
                : place.internationalPhoneNumber();

        return new HospitalDetailResponse(
                place.id(),
                place.displayName() != null ? place.displayName().text() : "이름 없음",
                place.formattedAddress(),
                callNumber,
                businessHours,
                operationLabel,
                is24hr,
                distanceMeter
        );
    }
}