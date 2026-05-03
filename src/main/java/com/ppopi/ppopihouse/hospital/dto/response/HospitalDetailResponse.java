package com.ppopi.ppopihouse.hospital.dto.response;

import com.ppopi.ppopihouse.hospital.domain.Hospital;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HospitalDetailResponse {

    private Long hospitalId;
    private String name;
    private String address;
    private String callNumber;
    private String businessHours;
    private String operationLabel;
    private boolean is24hr;
    private long distanceMeter;

    public static HospitalDetailResponse from(
            Hospital hospital,
            long distanceMeter,
            String operationLabel
    ) {
        return new HospitalDetailResponse(
                hospital.getHospitalId(),
                hospital.getName(),
                hospital.getAddress(),
                hospital.getCallNumber(),
                hospital.getBusinessHours(),
                operationLabel,
                hospital.is24hr(),
                distanceMeter
        );
    }
}