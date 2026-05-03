package com.ppopi.ppopihouse.diagnosis.repository;

import com.ppopi.ppopihouse.diagnosis.domain.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {

    Optional<Diagnosis> findTopByPet_PetIdAndDiagnosisDateOrderByDiagnosisIdDesc(
            Long petId,
            LocalDate diagnosisDate
    );
}