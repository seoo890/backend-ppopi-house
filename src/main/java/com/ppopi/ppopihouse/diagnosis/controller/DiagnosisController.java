package com.ppopi.ppopihouse.diagnosis.controller;

import com.ppopi.ppopihouse.diagnosis.dto.response.DiagnosisResponse;
import com.ppopi.ppopihouse.diagnosis.dto.response.RecentDiagnosisResponse;
import com.ppopi.ppopihouse.diagnosis.service.DiagnosisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diagnoses")
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DiagnosisResponse diagnose(
            @RequestParam Long petId,
            @RequestPart("image") MultipartFile image,
            @RequestParam List<Long> symptomIds
    ) {
        return diagnosisService.diagnose(petId, image, symptomIds);
    }

    @GetMapping("/today")
    public RecentDiagnosisResponse getTodayDiagnosis(
            @AuthenticationPrincipal Long memberId,
            @RequestParam Long petId,
            @RequestParam LocalDate date
    ) {
        return diagnosisService.getTodayDiagnosis(memberId, petId, date);
    }
}