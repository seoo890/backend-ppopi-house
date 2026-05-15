package com.ppopi.ppopihouse.diagnosis.dto.response;

import com.ppopi.ppopihouse.diary.dto.DiaryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter // 추가
@Builder // 빌더 패턴 도입
@AllArgsConstructor
public class DiagnosisResponse {

    private String imageUrl;
    private String triage;
    private String diseaseName;
    private String affectedArea;
    private int confidence;
    private String guidanceAction;
    private String guidanceMessage;
    private String guidanceWarning;
    // [추가] 진단 시 선택했던 증상(체크리스트) 상세 정보
    private List<DiaryDto.SymptomResponse> symptoms;
}