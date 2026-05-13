package com.ppopi.ppopihouse.diagnosis.repository;

import com.ppopi.ppopihouse.diagnosis.domain.EyeDiseaseCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EyeDiseaseCodeRepository extends JpaRepository<EyeDiseaseCode, Long> {

    Optional<EyeDiseaseCode> findByDiseaseNameAndInputSpeciesAndAffectedArea(
            String diseaseName,
            String inputSpecies,
            String affectedArea
    );

    Optional<EyeDiseaseCode> findByDiseaseNameAndInputSpecies(
            String diseaseName,
            String inputSpecies
    );
}