package com.ppopi.ppopihouse.diary.controller;

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
            @AuthenticationPrincipal Long memberId,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(diaryService.findMonthlyRecords(memberId, year, month));
    }

    /**
     * 2. 일별 상세 조회
     */
    @GetMapping
    public ResponseEntity<List<DiaryResponseDto.DayDetailResponse>> getDailyDiaries(
            @AuthenticationPrincipal Long memberId,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam int day) {
        return ResponseEntity.ok(diaryService.findDailyDiaries(memberId, year, month, day));
    }

    @Operation(summary = "증상 체크리스트 목록 조회", description = "다이어리 생성 시 필요한 증상 목록을 조회합니다.")
    @GetMapping("/checks")
    public ResponseEntity<List<DiaryDto.CheckCodeResponse>> getCheckCodes() {
        return ResponseEntity.ok(diaryService.findAllCheckCodes());
    }

    /**
     * 다이어리 추가
     */
    @PostMapping
    public ResponseEntity<Void> createDiary(
            @AuthenticationPrincipal Long memberId,
            @RequestBody DiaryDto.CreateRequest request) {
        diaryService.saveDiary(memberId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 다이어리 수정
     */
    @PutMapping("/{diaryId}")
    public ResponseEntity<Void> updateDiary(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long diaryId,
            @RequestBody DiaryDto.UpdateRequest request) {

        diaryService.updateDiary(memberId, diaryId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 다이어리 삭제
     */
    @DeleteMapping("/{diaryId}")
    public ResponseEntity<Void> deleteDiary(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long diaryId) {
        diaryService.deleteDiary(memberId, diaryId);
        return ResponseEntity.ok().build();
    }



}