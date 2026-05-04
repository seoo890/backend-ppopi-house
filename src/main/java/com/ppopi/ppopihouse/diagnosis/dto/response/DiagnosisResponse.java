package com.ppopi.ppopihouse.diagnosis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
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
}