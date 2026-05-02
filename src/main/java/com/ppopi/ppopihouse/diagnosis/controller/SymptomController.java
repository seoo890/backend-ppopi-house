package com.ppopi.ppopihouse.diagnosis.controller;

import com.ppopi.ppopihouse.diagnosis.dto.response.SymptomResponse;
import com.ppopi.ppopihouse.diagnosis.service.SymptomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/symptoms")
@RequiredArgsConstructor
public class SymptomController {

    private final SymptomService symptomService;

    @GetMapping
    public List<SymptomResponse> getSymptoms() {
        return symptomService.getSymptoms();
    }

}