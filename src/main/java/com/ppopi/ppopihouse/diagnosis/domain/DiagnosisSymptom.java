package com.ppopi.ppopihouse.diagnosis.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "diagnosis_symptom")
@Getter
@Setter
@NoArgsConstructor
public class DiagnosisSymptom {

    @EmbeddedId
    private DiagnosisSymptomId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("diagnosisId")
    @JoinColumn(name = "diagnosis_id")
    private Diagnosis diagnosis;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("symptomId")
    @JoinColumn(name = "symptom_id")
    private Symptom symptom;
}