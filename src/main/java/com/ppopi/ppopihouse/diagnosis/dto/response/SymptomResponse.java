package com.ppopi.ppopihouse.diagnosis.dto.response;

import com.ppopi.ppopihouse.diagnosis.domain.EyeSymptom;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SymptomResponse {

    private Long id;
    private String name;

    public static SymptomResponse from(EyeSymptom symptom) {
        return new SymptomResponse(symptom.getId(), symptom.getName());
    }
}