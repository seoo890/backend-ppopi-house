package com.ppopi.ppopihouse.hospital.controller;

import com.ppopi.ppopihouse.hospital.dto.request.HospitalSearchRequest;
import com.ppopi.ppopihouse.hospital.dto.response.HospitalDetailResponse;
import com.ppopi.ppopihouse.hospital.dto.response.HospitalListResponse;
import com.ppopi.ppopihouse.hospital.service.HospitalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hospitals")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    @Operation(
            summary = "지도 영역 내 동물병원 검색",
            description = """
                    현재 지도 화면에 보이는 영역(bounds) 안의 동물병원 목록을 조회합니다.
                    
                    클라이언트는 지도 이동 또는 확대/축소 시 현재 지도 영역의 northeast, southwest 좌표를 전달합니다.
                    emergencyOnly가 true이면 24시간 운영 병원만 조회합니다.
                    limit 값으로 반환 개수를 제한할 수 있습니다.
                    """
    )
    @PostMapping("/search")
    public List<HospitalListResponse> getHospitals(
            @RequestBody HospitalSearchRequest request
    ) {
        return hospitalService.getHospitals(request);
    }

    @Operation(
            summary = "동물병원 상세 조회",
            description = """
                    특정 동물병원의 상세 정보를 조회합니다.
                    
                    centerLat, centerLng는 사용자의 현재 위치 또는 지도 중심 좌표입니다.
                    서버는 해당 좌표와 병원 위치를 기준으로 거리 정보를 계산하여 반환할 수 있습니다.
                    """
    )
    @GetMapping("/{hospitalId}")
    public HospitalDetailResponse getHospital(
            @Parameter(description = "조회할 병원 Place ID")
            @PathVariable String hospitalId,

            @Parameter(description = "사용자 또는 지도 중심 위도", example = "37.5665")
            @RequestParam double centerLat,

            @Parameter(description = "사용자 또는 지도 중심 경도", example = "126.9780")
            @RequestParam double centerLng
    ) {
        return hospitalService.getHospital(hospitalId, centerLat, centerLng);
    }

}