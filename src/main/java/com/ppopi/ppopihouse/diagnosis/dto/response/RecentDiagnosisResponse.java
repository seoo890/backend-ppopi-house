package com.ppopi.ppopihouse.diagnosis.dto.response;

import com.ppopi.ppopihouse.diagnosis.domain.Diagnosis;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecentDiagnosisResponse {

    private boolean hasDiagnosis;

    private Long diagnosisId;
    private String imageUrl;

    private Long diseaseId;
    private String diseaseName;

    private String triageKey;
    private float triageConfidence;

    private String guideMsg;
    private String guideWarn;
    private String guideAction;

    public static RecentDiagnosisResponse empty() {
        return new RecentDiagnosisResponse(
                false,
                null, null,
                null, null,
                null, 0,
                null, null, null
        );
    }

    public static RecentDiagnosisResponse from(Diagnosis d) {
        return new RecentDiagnosisResponse(
                true,
                d.getDiagnosisId(),
                d.getImageUrl(),
                d.getDisease().getDiseaseId(),
                d.getDisease().getDiseaseName(),
                d.getTriageKey(),
                d.getTriageConfidence(),
                d.getGuideMsg(),
                d.getGuideWarn(),
                d.getGuideAction()
        );
    }
}