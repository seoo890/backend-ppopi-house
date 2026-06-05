package com.ppopi.ppopihouse.diagnosis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor // 🌟 서비스 레이어에서 8개 인자로 생성자를 호출할 수 있도록 명시적 선언
public class DiagnosisResponse {

    private String imageUrl;
    private String triage;
    private String diseaseName;
    private String affectedArea;
    private int confidence;
    private String guidanceAction;
    private String guidanceMessage;
    private String guidanceWarning;
}
