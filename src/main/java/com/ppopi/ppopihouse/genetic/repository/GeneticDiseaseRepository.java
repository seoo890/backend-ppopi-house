package com.ppopi.ppopihouse.genetic.repository;

import com.ppopi.ppopihouse.genetic.domain.GeneticDisease;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GeneticDiseaseRepository extends JpaRepository<GeneticDisease, Long> {

    @Query("""
    SELECT g
    FROM GeneticDisease g
    WHERE LOWER(g.breed) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(g.diseaseName) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(g.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
""")
    List<GeneticDisease> searchByKeyword(String keyword, Pageable pageable);
}