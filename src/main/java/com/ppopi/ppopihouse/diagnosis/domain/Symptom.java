package com.ppopi.ppopihouse.diagnosis.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name = "symptom")
@Getter
@Setter
@NoArgsConstructor
public class Symptom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "symptom_id")
    private Long symptomId;

    @Column(nullable = false)
    private String name;

}