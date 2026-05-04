package com.ppopi.ppopihouse.diagnosis.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DiagnosisSymptomId implements Serializable {

    private Long diagnosisId;
    private Long symptomId;
}