package com.ppopi.ppopihouse.diagnosis.repository;

import com.ppopi.ppopihouse.diagnosis.domain.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
}