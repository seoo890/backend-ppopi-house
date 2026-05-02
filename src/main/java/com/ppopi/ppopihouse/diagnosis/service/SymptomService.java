package com.ppopi.ppopihouse.diagnosis.service;

import com.ppopi.ppopihouse.diagnosis.domain.EyeSymptom;
import com.ppopi.ppopihouse.diagnosis.dto.response.SymptomResponse;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class SymptomService {

    public List<SymptomResponse> getSymptoms() {
        return Arrays.stream(EyeSymptom.values())
                .map(SymptomResponse::from)
                .toList();
    }

}