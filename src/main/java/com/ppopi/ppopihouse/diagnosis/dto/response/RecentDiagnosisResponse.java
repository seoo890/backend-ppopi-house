package com.ppopi.ppopihouse.diagnosis.dto.response;

import com.ppopi.ppopihouse.diagnosis.domain.Diagnosis;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RecentDiagnosisResponse {

    private boolean hasDiagnosis;

    private String imageUrl;
    private String triage;
    private String diseaseName;
    private String affectedArea;
    private int confidence;

    private String guidanceAction;
    private String guidanceMessage;
    private String guidanceWarning;

    private List<SymptomChecklist> symptoms;

    @Getter
    @AllArgsConstructor
    public static class SymptomChecklist {
        private Long symptomId;
        private String description;
        private boolean checked;
    }

    public static RecentDiagnosisResponse empty(List<SymptomChecklist> symptoms) {
        return new RecentDiagnosisResponse(
                false,
                null,
                null,
                null,
                null,
                0,
                null,
                null,
                null,
                symptoms
        );
    }

}