package com.ppopi.ppopihouse.genetic.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "genetic_disease")
public class GeneticDisease {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "genetic_disease_id")
    private Long geneticDiseaseId;

    @Column(nullable = false)
    private String breed;

    @Column(nullable = false)
    private String diseaseName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

}