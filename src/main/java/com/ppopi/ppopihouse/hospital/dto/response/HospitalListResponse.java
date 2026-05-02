package com.ppopi.ppopihouse.hospital.dto.response;

import com.ppopi.ppopihouse.hospital.dto.projection.HospitalDistanceProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HospitalListResponse {

    private Long hospitalId;
    private String name;
    private double latitude;
    private double longitude;
    private boolean is24hr;
    private long distanceMeter;

    public static HospitalListResponse from(HospitalDistanceProjection hospital) {
        return new HospitalListResponse(
                hospital.getHospitalId(),
                hospital.getName(),
                hospital.getLatitude(),
                hospital.getLongitude(),
                Boolean.TRUE.equals(hospital.getIs24hr()),
                hospital.getDistanceMeter()
        );
    }
}