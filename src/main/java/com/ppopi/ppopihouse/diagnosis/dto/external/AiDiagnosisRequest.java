package com.ppopi.ppopihouse.diagnosis.dto.external;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AiDiagnosisRequest {

    private String imageUrl;
    private String species;
    private String breed;
    private int age;
    private String sex;
    private List<String> symptoms;
}