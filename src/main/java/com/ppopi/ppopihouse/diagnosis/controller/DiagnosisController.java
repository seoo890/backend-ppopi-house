package com.ppopi.ppopihouse.diagnosis.controller;

import com.ppopi.ppopihouse.auth.security.CustomUserDetails;
import com.ppopi.ppopihouse.diagnosis.dto.response.DiagnosisResponse;
import com.ppopi.ppopihouse.diagnosis.dto.response.RecentDiagnosisResponse;
import com.ppopi.ppopihouse.diagnosis.service.DiagnosisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Operation(
            summary = "AI 진단 요청",
            description = """
                    반려동물 사진과 선택한 증상 목록을 기반으로 AI 진단을 요청합니다.
                    
                    요청 흐름:
                    1. 클라이언트가 petId, image, symptomIds를 전달합니다.
                    2. 서버가 이미지 유효성 검사를 수행합니다.
                    3. 유효한 이미지인 경우 AI 서버에 진단을 요청합니다.
                    4. AI 응답 결과를 DB에 저장한 뒤 클라이언트에 진단 결과를 반환합니다.
                    
                    요청 형식은 multipart/form-data입니다.
                    """
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DiagnosisResponse diagnose(
            @Parameter(description = "진단 대상 반려동물 ID", example = "1")
            @RequestParam Long petId,

            @Parameter(description = "진단에 사용할 반려동물 이미지 파일")
            @RequestParam("image") MultipartFile image,

            @Parameter(description = "선택한 증상 ID 목록", example = "1,2,3")
            @RequestParam(required = false) List<Long> symptomIds,

            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return diagnosisService.diagnose(userDetails.getMemberId(), petId, image, symptomIds);
    }

    @Operation(
            summary = "오늘의 진단 결과 조회",
            description = """
                    특정 반려동물의 특정 날짜 진단 결과를 조회합니다.
                    
                    주로 홈 화면 또는 진단 완료 후 최근 진단 정보를 다시 보여줄 때 사용합니다.
                    인증된 회원의 반려동물 진단 기록만 조회할 수 있습니다.
                    """
    )
    @GetMapping("/today")
    public RecentDiagnosisResponse getTodayDiagnosis(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @Parameter(description = "조회할 반려동물 ID", example = "1")
            @RequestParam Long petId,

            @Parameter(description = "조회할 날짜", example = "2026-05-04")
            @RequestParam LocalDate date
    ) {
        return diagnosisService.getTodayDiagnosis(userDetails.getMemberId(), petId, date);
    }
}