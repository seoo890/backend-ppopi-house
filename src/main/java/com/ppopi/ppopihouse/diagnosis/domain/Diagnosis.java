package com.ppopi.ppopihouse.diagnosis.domain;

import com.ppopi.ppopihouse.pet.domain.Pet;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "diagnosis")
@Getter
@Setter
@NoArgsConstructor
public class Diagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diagnosis_id")
    private Long diagnosisId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disease_id", nullable = false)
    private EyeDiseaseCode disease;

    @Column(name = "diagnosis_date", nullable = false)
    private LocalDate diagnosisDate;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "triage_key", nullable = false)
    private String triageKey;

    @Column(name = "triage_confidence", nullable = false)
    private float triageConfidence;

    @Column(name = "guide_msg", nullable = false)
    private String guideMsg;

    @Column(name = "guide_warn", nullable = false)
    private String guideWarn;

    @Column(name = "guide_action", nullable = false)
    private String guideAction;

    @Column(name = "symptom_ids")
    private String symptomIds;
}