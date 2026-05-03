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
            summary = "품종 목록 조회",
            description = """
                    반려동물 등록 시 선택할 수 있는 품종 목록을 조회합니다.
                    
                    species 값에 따라 강아지 또는 고양이 품종 목록을 반환합니다.
                    
                    - DOG: 강아지 품종 목록
                    - CAT: 고양이 품종 목록
                    """
    )
    @GetMapping("/breeds")
    public List<String> getBreeds(
            @Parameter(description = "반려동물 종", example = "DOG")
            @RequestParam PetSpecies species
    ) {
        return petService.getBreeds(species.name());
    }

    @Operation(
            summary = "반려동물 등록",
            description = """
                    로그인한 회원의 반려동물을 등록합니다.
                    
                    등록 정보에는 이름, 종, 품종, 출생연도, 성별, 색상 정보가 포함됩니다.
                    등록 완료 후 클라이언트는 홈 화면 또는 반려동물 목록 화면으로 이동할 수 있습니다.
                    """
    )
    @PostMapping
    public ResponseEntity<String> createPet(
            @Parameter(hidden = true)
            @AuthenticationPrincipal Long memberId,

            @RequestBody PetCreateRequest request
    ) {
        petService.createPet(memberId, request);
        return ResponseEntity.ok("ok");
    }
}