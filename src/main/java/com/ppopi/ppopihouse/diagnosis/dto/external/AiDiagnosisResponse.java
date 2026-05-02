package com.ppopi.ppopihouse.diagnosis.dto.external;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AiDiagnosisResponse {

    private String disease;
    private String triage;
    private float triageConfidence;
    private String familyLabel;
    private String guidanceMessage;
    private String guidanceAction;
    private String guidanceWarning;
}