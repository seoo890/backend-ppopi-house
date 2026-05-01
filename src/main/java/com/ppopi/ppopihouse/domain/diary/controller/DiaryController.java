package com.ppopi.ppopihouse.domain.diary.controller;

import com.ppopi.ppopihouse.domain.diary.dto.DiaryRequestDto;
import com.ppopi.ppopihouse.domain.diary.dto.DiaryResponseDto;
import com.ppopi.ppopihouse.domain.diary.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    /**
     * 1. 펫 리스트 요청 (특정 사용자의 펫 아이디, 이름, 색깔)
     */
    @GetMapping("/pets")
    public ResponseEntity<List<DiaryResponseDto.PetSummary>> getPetList(
            @RequestParam Long memberId) { // 파라미터 추가
        return ResponseEntity.ok(diaryService.findPetSummaries(memberId));
    }

    /**
     * 2. 월간 데이터 요청 (예: 2026년 3월 데이터 요청)
     * 결과: 일자별 다이어리가 존재하는 색깔 리스트
     */
    @GetMapping("/monthly")
    public ResponseEntity<List<DiaryResponseDto.MonthlyRecord>> getMonthlyRecords(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(required = false) Long petId) { // 선택 파라미터 추가
        return ResponseEntity.ok(diaryService.findMonthlyColors(year, month, petId));
    }



    /**
     * 3. 일별 데이터 요청 (예: 2026-03-04 데이터 요청)
     * 결과: 해당 일자의 다이어리 리스트
     */
    @GetMapping("/daily")
    public ResponseEntity<List<DiaryResponseDto.DiaryDetail>> getDailyDiaries(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long petId) { // 선택 파라미터 추가
        return ResponseEntity.ok(diaryService.findDiariesByDate(date, petId));
    }

    /**
     * 4. 다이어리 추가 (펫ID, 메모, 체크리스트)
     */
    @PostMapping
    public ResponseEntity<Void> addDiary(@RequestBody DiaryRequestDto.Create request) {
        diaryService.saveDiary(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 5. 특정 다이어리 수정 (다이어리 ID, 메모, 체크리스트)
     */
    @PutMapping("/{diaryId}")
    public ResponseEntity<Void> updateDiary(
            @PathVariable Long diaryId,
            @RequestBody DiaryRequestDto.Update request) {
        diaryService.updateDiary(diaryId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 6. 특정 다이어리 삭제 (다이어리 ID)
     */
    @DeleteMapping("/{diaryId}")
    public ResponseEntity<Void> deleteDiary(@PathVariable Long diaryId) {
        diaryService.deleteDiary(diaryId);
        return ResponseEntity.noContent().build();
    }
}