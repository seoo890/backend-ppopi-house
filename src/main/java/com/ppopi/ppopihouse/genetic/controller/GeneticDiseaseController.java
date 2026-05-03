package com.ppopi.ppopihouse.genetic.controller;

import com.ppopi.ppopihouse.genetic.dto.response.GeneticDiseaseResponse;
import com.ppopi.ppopihouse.genetic.service.GeneticDiseaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/genetic-diseases")
@RequiredArgsConstructor
public class GeneticDiseaseController {

    private final GeneticDiseaseService geneticDiseaseService;

    @Operation(
            summary = "랜덤 유전병 정보 조회",
            description = """
                    홈 화면 또는 콘텐츠 영역에서 보여줄 유전병 정보를 랜덤으로 조회합니다.
                    
                    품종과 무관하게 여러 유전병 정보를 추천 형태로 반환합니다.
                    """
    )
    @GetMapping("/random")
    public List<GeneticDiseaseResponse> getRandomDiseases() {
        return geneticDiseaseService.getRandomDiseases();
    }

    @Operation(
            summary = "유전병 검색",
            description = """
                    키워드를 기반으로 유전병 정보를 검색합니다.
                    
                    품종명 또는 질병명, 설명에 대한 키워드를 검색에 사용할 수 있습니다.
                    예: 말티즈, 슬개골, 피부염
                    """
    )
    @GetMapping("/search")
    public List<GeneticDiseaseResponse> searchDiseases(
            @Parameter(description = "검색 키워드", example = "말티즈")
            @RequestParam String keyword
    ) {
        return geneticDiseaseService.searchDiseases(keyword);
    }
}