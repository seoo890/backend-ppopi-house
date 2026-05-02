package com.ppopi.ppopihouse.hospital.service;

import com.ppopi.ppopihouse.hospital.dto.request.HospitalSearchRequest;
import com.ppopi.ppopihouse.hospital.dto.response.HospitalDetailResponse;
import com.ppopi.ppopihouse.hospital.dto.response.HospitalListResponse;

import java.util.List;

public interface HospitalService {

    List<HospitalListResponse> getHospitals(HospitalSearchRequest request);

    HospitalDetailResponse getHospital(Long hospitalId, double centerLat, double centerLng);
}