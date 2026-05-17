package com.ppopi.ppopihouse.diary.controller;

import com.ppopi.ppopihouse.auth.security.CustomUserDetails;
import com.ppopi.ppopihouse.diary.dto.DiaryDto;
import com.ppopi.ppopihouse.diary.dto.DiaryResponseDto;
import com.ppopi.ppopihouse.diary.service.DiaryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // 추가
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    /**
     * 1. 월별 기록 조회
     */
    @GetMapping("/month")
    public ResponseEntity<DiaryResponseDto.MonthlyResponse> getMonthlyDiaries(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(diaryService.findMonthlyRecords(userDetails.getMemberId(), year, month));
    }

    /**
     * 2. 일별 상세 조회
     */
    @GetMapping
    public ResponseEntity<List<DiaryResponseDto.DayDetailResponse>> getDailyDiaries(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam int day) {
        return ResponseEntity.ok(diaryService.findDailyDiaries(userDetails.getMemberId(), year, month, day));
    }

    @Operation(
            summary = "일반 건강 체크리스트 목록 조회", // 기존: 증상 체크리스트 목록 조회
            description = "다이어리 생성 시 필요한 일반 건강 항목(식욕, 구토 등) 목록을 조회합니다."
    )
    @GetMapping("/checks")
    public ResponseEntity<List<DiaryDto.CheckCodeResponse>> getCheckCodes() {
        return ResponseEntity.ok(diaryService.findAllCheckCodes());
    }

    /**
     * 다이어리 추가
     */
    @PostMapping
    public ResponseEntity<Void> createDiary(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody DiaryDto.CreateRequest request) {
        diaryService.saveDiary(userDetails.getMemberId(), request);
        return ResponseEntity.ok().build();
    }

    /**
     * 다이어리 수정
     */
    @PutMapping("/{diaryId}")
    public ResponseEntity<Void> updateDiary(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long diaryId,
            @RequestBody DiaryDto.UpdateRequest request) {

        diaryService.updateDiary(userDetails.getMemberId(), diaryId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 다이어리 삭제
     */
    @DeleteMapping("/{diaryId}")
    public ResponseEntity<Void> deleteDiary(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long diaryId) {
        diaryService.deleteDiary(userDetails.getMemberId(), diaryId);
        return ResponseEntity.ok().build();
    }
}