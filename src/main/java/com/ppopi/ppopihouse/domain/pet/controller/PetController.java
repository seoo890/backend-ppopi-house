package com.ppopi.ppopihouse.domain.pet.controller;

import com.ppopi.ppopihouse.domain.pet.dto.PetRegisterRequestDto;
import com.ppopi.ppopihouse.domain.pet.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Pet API", description = "반려동물 등록 및 관리")
@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @PostMapping
    @Operation(summary = "반려동물 등록", description = "반려동물 정보를 입력받아 DB에 저장합니다.")
    public ResponseEntity<String> registerPet(@RequestBody PetRegisterRequestDto requestDto) {
        // 서비스 호출 및 PK 반환
        Long savedId = petService.registerPet(requestDto);

        // 성공 시 200 OK와 저장된 ID 반환
        return ResponseEntity.ok("Successfully registered. ID: " + savedId);
    }
}