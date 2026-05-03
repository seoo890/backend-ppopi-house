package com.ppopi.ppopihouse.pet.controller;

import com.ppopi.ppopihouse.diary.dto.DiaryDto;
import com.ppopi.ppopihouse.pet.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pets") // 피드백 반영: Pet 도메인으로 분리
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    /**
     * 현재 로그인한 회원의 반려동물 목록 조회
     * GET /pets
     */
    @GetMapping
    public ResponseEntity<List<DiaryDto.PetSummary>> getMyPets(
            @AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(petService.findPetSummaries(memberId));
    }
}