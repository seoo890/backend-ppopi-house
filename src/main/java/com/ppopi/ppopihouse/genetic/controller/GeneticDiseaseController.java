package com.ppopi.ppopihouse.genetic.controller;

import com.ppopi.ppopihouse.genetic.dto.response.GeneticDiseaseResponse;
import com.ppopi.ppopihouse.genetic.service.GeneticDiseaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/genetic-diseases")
@RequiredArgsConstructor
public class GeneticDiseaseController {

    private final GeneticDiseaseService geneticDiseaseService;

    @GetMapping("/random")
    public List<GeneticDiseaseResponse> getRandomDiseases() {
        return geneticDiseaseService.getRandomDiseases();
    }

    @GetMapping("/search")
    public List<GeneticDiseaseResponse> searchDiseases(
            @RequestParam String keyword
    ) {
        return geneticDiseaseService.searchDiseases(keyword);
    }
}