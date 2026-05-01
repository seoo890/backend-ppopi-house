package com.ppopi.ppopihouse.diagnosis.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "eye_disease_code")
@Getter
@Setter
@NoArgsConstructor
public class EyeDiseaseCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "disease_id")
    private Long diseaseId;

    @Column(name = "input_species", nullable = false)
    private String inputSpecies;

    @Column(name = "affected_area", nullable = false)
    private String affectedArea;

    @Column(name = "disease_name", nullable = false)
    private String diseaseName;
}