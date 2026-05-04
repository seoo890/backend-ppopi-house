package com.ppopi.ppopihouse.hospital.service;

import com.ppopi.ppopihouse.hospital.domain.Hospital;
import com.ppopi.ppopihouse.hospital.dto.request.HospitalSearchRequest;
import com.ppopi.ppopihouse.hospital.dto.response.HospitalDetailResponse;
import com.ppopi.ppopihouse.hospital.dto.response.HospitalListResponse;
import com.ppopi.ppopihouse.hospital.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
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
                        request.getCenter().getLng()
                )
                .stream()
                .map(HospitalListResponse::from)
                .toList();
    }

    @Override
    public HospitalDetailResponse getHospital(Long hospitalId, double centerLat, double centerLng) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 병원입니다."));

        long distanceMeter = calculateDistanceMeter(
                centerLat,
                centerLng,
                hospital.getLatitude(),
                hospital.getLongitude()
        );

        String operationLabel = hospital.is24hr()
                ? "영업 중"
                : getOperationLabel(hospital.getBusinessHours());

        return HospitalDetailResponse.from(hospital, distanceMeter, operationLabel);
    }

    private long calculateDistanceMeter(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2)
                * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return Math.round(earthRadius * c);
    }

    private String getOperationLabel(String businessHours) {
        try {
            String[] parts = businessHours.split(" - ");
            LocalTime open = LocalTime.parse(parts[0]);
            LocalTime close = LocalTime.parse(parts[1]);

            LocalTime now = LocalTime.now();

            if (now.isAfter(open) && now.isBefore(close)) {
                return "영업 중";
            } else {
                return "영업 종료";
            }
        } catch (Exception e) {
            return "정보 없음";
        }
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