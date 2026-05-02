package com.ppopi.ppopihouse.hospital.service;

import com.ppopi.ppopihouse.hospital.dto.projection.HospitalDistanceProjection;
import com.ppopi.ppopihouse.hospital.dto.request.HospitalSearchRequest;
import com.ppopi.ppopihouse.hospital.dto.response.HospitalDetailResponse;
import com.ppopi.ppopihouse.hospital.dto.response.HospitalListResponse;
import com.ppopi.ppopihouse.hospital.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HospitalServiceImpl implements HospitalService {

    private static final int DEFAULT_LIMIT = 50;

    private final HospitalRepository hospitalRepository;

    @Override
    public List<HospitalListResponse> getHospitals(HospitalSearchRequest request) {
        validateSearchRequest(request);

        return hospitalRepository.findHospitalsInBoundsOrderByDistance(
                        request.getBounds().getSouthwest().getLat(),
                        request.getBounds().getNortheast().getLat(),
                        request.getBounds().getSouthwest().getLng(),
                        request.getBounds().getNortheast().getLng(),
                        request.getCenter().getLat(),
                        request.getCenter().getLng(),
                        Boolean.TRUE.equals(request.getEmergencyOnly()),
                        request.getLimit() == null ? DEFAULT_LIMIT : request.getLimit()
                )
                .stream()
                .map(HospitalListResponse::from)
                .toList();
    }

    @Override
    public HospitalDetailResponse getHospital(Long hospitalId, double centerLat, double centerLng) {
        HospitalDistanceProjection hospital = hospitalRepository.findHospitalDetailWithDistance(
                hospitalId,
                centerLat,
                centerLng
        );

        if (hospital == null) {
            throw new IllegalArgumentException("존재하지 않는 병원입니다.");
        }

        return HospitalDetailResponse.from(hospital);
    }

    private void validateSearchRequest(HospitalSearchRequest request) {
        if (request == null
                || request.getBounds() == null
                || request.getBounds().getNortheast() == null
                || request.getBounds().getSouthwest() == null
                || request.getCenter() == null) {
            throw new IllegalArgumentException("지도 검색 요청 값이 올바르지 않습니다.");
        }
    }
}