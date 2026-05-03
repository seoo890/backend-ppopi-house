package com.ppopi.ppopihouse.genetic.dto.response;

import com.ppopi.ppopihouse.genetic.domain.GeneticDisease;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneticDiseaseResponse {

    private Long geneticDiseaseId;
    private String breed;
    private String diseaseName;
    private String description;

    public static GeneticDiseaseResponse from(GeneticDisease disease) {
        return new GeneticDiseaseResponse(
                disease.getGeneticDiseaseId(),
                disease.getBreed(),
                disease.getDiseaseName(),
                disease.getDescription()
        );
    }
}