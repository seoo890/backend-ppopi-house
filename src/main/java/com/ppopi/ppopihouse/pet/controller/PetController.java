package com.ppopi.ppopihouse.pet.controller;

import com.ppopi.ppopihouse.auth.security.CustomUserDetails;
import com.ppopi.ppopihouse.diary.dto.DiaryDto;
import com.ppopi.ppopihouse.pet.dto.request.PetUpdateRequest;
import com.ppopi.ppopihouse.pet.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ppopi.ppopihouse.pet.domain.PetSpecies;
import com.ppopi.ppopihouse.pet.dto.request.PetCreateRequest;
import com.ppopi.ppopihouse.pet.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    /**
     * 현재 로그인한 회원의 반려동물 목록 조회
     * GET /pets
     */
    @GetMapping
    public ResponseEntity<List<DiaryDto.PetSummary>> getMyPets(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(petService.findPetSummaries(userDetails.getMemberId()));
    }

    @Operation(summary = "반려동물 정보 수정", description = "기존 등록된 반려동물의 정보를 수정합니다.")
    @PutMapping("/{petId}")
    public ResponseEntity<String> updatePet(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long petId,
            @RequestBody PetUpdateRequest request) {

        petService.updatePet(userDetails.getMemberId(), petId, request);
        return ResponseEntity.ok("success");
    }

    /**
     * 반려동물 정보 삭제
     * DELETE /pets/{petId}
     */
    @Operation(summary = "반려동물 정보 삭제", description = "등록된 반려동물 정보를 삭제합니다.")
    @DeleteMapping("/{petId}")
    public ResponseEntity<String> deletePet(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long petId) {

        petService.deletePet(userDetails.getMemberId(), petId);
        return ResponseEntity.ok("success");
    }

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
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody PetCreateRequest request
    ) {
        petService.createPet(userDetails.getMemberId(), request);
        return ResponseEntity.ok("ok");
    }
}