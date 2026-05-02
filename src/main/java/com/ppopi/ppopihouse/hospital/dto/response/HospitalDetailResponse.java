package com.ppopi.ppopihouse.hospital.dto.response;

import com.ppopi.ppopihouse.hospital.dto.projection.HospitalDistanceProjection;
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
    private boolean is24hr;
    private double latitude;
    private double longitude;
    private long distanceMeter;

    public static HospitalDetailResponse from(HospitalDistanceProjection hospital) {
        return new HospitalDetailResponse(
                hospital.getHospitalId(),
                hospital.getName(),
                hospital.getAddress(),
                hospital.getCallNumber(),
                hospital.getBusinessHours(),
                Boolean.TRUE.equals(hospital.getIs24hr()),
                hospital.getLatitude(),
                hospital.getLongitude(),
                hospital.getDistanceMeter()
        );
    }
}