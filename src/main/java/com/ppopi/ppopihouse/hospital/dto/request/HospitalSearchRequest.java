package com.ppopi.ppopihouse.hospital.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HospitalSearchRequest {

    private Bounds bounds;
    private Integer zoom;
    private Coordinate center;
    private Boolean emergencyOnly = false;
    private Integer limit = 50;

    @Getter
    @Setter
    public static class Bounds {
        private Coordinate northeast;
        private Coordinate southwest;
    }

    @Getter
    @Setter
    public static class Coordinate {
        private Double lat;
        private Double lng;
    }
}