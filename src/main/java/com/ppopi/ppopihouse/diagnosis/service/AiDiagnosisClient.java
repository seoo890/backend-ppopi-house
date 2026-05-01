
package com.ppopi.ppopihouse.diagnosis.service;

import com.ppopi.ppopihouse.diagnosis.dto.external.AiDiagnosisRequest;
import com.ppopi.ppopihouse.diagnosis.dto.external.AiDiagnosisResponse;

public interface AiDiagnosisClient {
    AiDiagnosisResponse diagnose(AiDiagnosisRequest request);

}
