
package com.ppopi.ppopihouse.diagnosis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DiagnosisResponse {

    private String diseaseName;
    private String triage;
    private float confidence;
    private String affectedArea;
    private String description;
    private String action;

}