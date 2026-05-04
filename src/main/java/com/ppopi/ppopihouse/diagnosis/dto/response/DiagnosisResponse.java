package com.ppopi.ppopihouse.diagnosis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DiagnosisResponse {

    private Summary summary;
    private ResultCard resultCard;

    @Getter
    @AllArgsConstructor
    public static class Summary {
        private String status;
        private String diseaseName;
        private int confidence;
    }

    @Getter
    @AllArgsConstructor
    public static class ResultCard {
        private String title;
        private String description;
        private String guideTitle;
        private String guideMessage;
    }
}