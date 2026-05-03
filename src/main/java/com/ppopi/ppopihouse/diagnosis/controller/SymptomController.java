package com.ppopi.ppopihouse.diagnosis.controller;

import com.ppopi.ppopihouse.diagnosis.dto.response.SymptomResponse;
import com.ppopi.ppopihouse.diagnosis.service.SymptomService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/symptoms")
@RequiredArgsConstructor
public class SymptomController {

    private final SymptomService symptomService;

    @Operation(
            summary = "증상 목록 조회",
            description = """
                    진단 요청 전 클라이언트에서 선택할 수 있는 증상 목록을 조회합니다.
                    
                    클라이언트는 이 API로 받은 symptomId를 진단 요청 API의 symptomIds에 담아 전달합니다.
                    """
    )
    @GetMapping
    public List<SymptomResponse> getSymptoms() {
        return symptomService.getSymptoms();
    }
}