package com.ppopi.ppopihouse.diagnosis.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class DiagnosisSymptomId implements Serializable {

    private Long diagnosisId;
    private Long symptomId;
}