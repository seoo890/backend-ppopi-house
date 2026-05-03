package com.ppopi.ppopihouse.hospital.controller;

import com.ppopi.ppopihouse.hospital.dto.request.HospitalSearchRequest;
import com.ppopi.ppopihouse.hospital.dto.response.HospitalDetailResponse;
import com.ppopi.ppopihouse.hospital.dto.response.HospitalListResponse;
import com.ppopi.ppopihouse.hospital.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hospitals")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    @PostMapping("/search")
    public List<HospitalListResponse> getHospitals(
            @RequestBody HospitalSearchRequest request
    ) {
        return hospitalService.getHospitals(request);
    }

    @GetMapping("/{hospitalId}")
    public HospitalDetailResponse getHospital(
            @PathVariable Long hospitalId,
            @RequestParam double centerLat,
            @RequestParam double centerLng
    ) {
        return hospitalService.getHospital(hospitalId, centerLat, centerLng);
    }

}