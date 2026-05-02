package com.ppopi.ppopihouse.hospital.dto.projection;

public interface HospitalDistanceProjection {

    Long getHospitalId();

    String getName();

    String getAddress();

    String getCallNumber();

    String getBusinessHours();

    Boolean getIs24hr();

    Double getLatitude();

    Double getLongitude();

    Long getDistanceMeter();
}