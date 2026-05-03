package com.ppopi.ppopihouse.pet.controller;

import com.ppopi.ppopihouse.pet.domain.PetSpecies;
import com.ppopi.ppopihouse.pet.dto.request.PetCreateRequest;
import com.ppopi.ppopihouse.pet.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @Operation(
            summary = "품종 리스트 조회",
            description = """
        반려동물 등록 시 사용할 품종 리스트를 반환합니다.
        
        - DOG: 강아지 품종 목록
        - CAT: 고양이 품종 목록
        """
    )
    @GetMapping("/breeds")
    public List<String> getBreeds(
            @Parameter(description = "반려동물 종 (DOG 또는 CAT)", example = "DOG")
            @RequestParam PetSpecies species
    ) {
        return petService.getBreeds(species.name());
    }

    @Operation(summary = "반려동물 등록")
    @PostMapping
    public ResponseEntity<String> createPet(
            @AuthenticationPrincipal Long memberId,
            @RequestBody PetCreateRequest request
    ) {
        petService.createPet(memberId, request);
        return ResponseEntity.ok("ok");
    }
}