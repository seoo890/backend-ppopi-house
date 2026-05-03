package com.ppopi.ppopihouse.genetic.service;

import com.ppopi.ppopihouse.genetic.domain.GeneticDisease;
import com.ppopi.ppopihouse.genetic.dto.response.GeneticDiseaseResponse;
import com.ppopi.ppopihouse.genetic.repository.GeneticDiseaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GeneticDiseaseService {

    private static final int DEFAULT_LIMIT = 3;

    private final GeneticDiseaseRepository geneticDiseaseRepository;

    public List<GeneticDiseaseResponse> getRandomDiseases() {
        List<GeneticDisease> diseases = geneticDiseaseRepository.findAll();

        if (diseases.isEmpty()) {
            return null;
        }

        Collections.shuffle(diseases);

        return diseases.stream()
                .limit(DEFAULT_LIMIT)
                .map(GeneticDiseaseResponse::from)
                .toList();
    }

    public List<GeneticDiseaseResponse> searchDiseases(String keyword) {
        List<GeneticDisease> diseases =
                geneticDiseaseRepository.searchByKeyword(keyword, PageRequest.of(0, 3));

        if (diseases.isEmpty()) {
            return null;
        }

        return diseases.stream()
                .map(GeneticDiseaseResponse::from)
                .toList();
    }
}