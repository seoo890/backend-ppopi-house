package com.ppopi.ppopihouse.domain.diary.service;

import com.ppopi.ppopihouse.domain.diary.dto.DiaryRequestDto;
import com.ppopi.ppopihouse.domain.diary.dto.DiaryResponseDto;
import com.ppopi.ppopihouse.domain.diary.entity.DiaryEntry;
import com.ppopi.ppopihouse.domain.diary.repository.DiaryRepository;
import com.ppopi.ppopihouse.domain.pet.entity.Pet;
import com.ppopi.ppopihouse.domain.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final PetRepository petRepository;

    /**
     * 1. 특정 사용자의 펫 리스트 조회 (ID, 이름, 색깔)
     */
    public List<DiaryResponseDto.PetSummary> findPetSummaries(Long memberId) {
        return petRepository.findAllByMemberId(memberId).stream()
                .map(pet -> DiaryResponseDto.PetSummary.builder()
                        .petId(pet.getPetId())
                        .name(pet.getName())
                        .color(pet.getColor())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 2. 월간 데이터 조회 (일자별 다이어리 존재 펫들의 색깔 리스트)
     */
    public List<DiaryResponseDto.MonthlyRecord> findMonthlyColors(int year, int month, Long petId) {
        LocalDate start = YearMonth.of(year, month).atDay(1);
        LocalDate end = YearMonth.of(year, month).atEndOfMonth();

        List<DiaryEntry> entries = (petId != null)
                ? diaryRepository.findAllByPetIdAndEntryDateBetween(petId, start, end)
                : diaryRepository.findAllByEntryDateBetween(start, end);

        Map<Long, Pet> petMap = petRepository.findAll().stream()
                .collect(Collectors.toMap(Pet::getPetId, pet -> pet));

        return entries.stream()
                .collect(Collectors.groupingBy(DiaryEntry::getEntryDate))
                .entrySet().stream()
                .map(entry -> DiaryResponseDto.MonthlyRecord.builder()
                        .date(entry.getKey())
                        .pets(entry.getValue().stream()
                                .map(d -> {
                                    Pet pet = petMap.get(d.getPetId());
                                    return DiaryResponseDto.PetSummary.builder()
                                            .petId(d.getPetId())
                                            .name(pet != null ? pet.getName() : "Unknown")
                                            .color(pet != null ? pet.getColor() : 0)
                                            .build();
                                })
                                .distinct()
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 3. 일별 다이어리 리스트 조회
     */
    public List<DiaryResponseDto.DiaryDetail> findDiariesByDate(LocalDate date, Long petId) {
        List<DiaryEntry> entries = (petId != null)
                ? diaryRepository.findAllByPetIdAndEntryDate(petId, date)
                : diaryRepository.findAllByEntryDate(date);

        return entries.stream().map(this::convertToDetailDto).collect(Collectors.toList());
    }

    /**
     * 4. 다이어리 추가
     */
    @Transactional
    public void saveDiary(DiaryRequestDto.Create request) {
        DiaryEntry diaryEntry = DiaryEntry.builder()
                .petId(request.getPetId())
                .diagnosisId(request.getDiagnosisId())
                .entryDate(request.getEntryDate())
                .memo(request.getMemo())
                .checkIds(request.getCheckList())
                .build();
        diaryRepository.save(diaryEntry);
    }

    /**
     * 5. 다이어리 수정
     */
    @Transactional
    public void updateDiary(Long diaryId, DiaryRequestDto.Update request) {
        DiaryEntry diaryEntry = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 다이어리가 존재하지 않습니다. ID: " + diaryId));

        diaryEntry.updateDiary(request.getMemo(), request.getCheckList());
    }

    /**
     * 6. 다이어리 삭제
     */
    @Transactional
    public void deleteDiary(Long diaryId) {
        diaryRepository.deleteById(diaryId);
    }

    /**
     * 내부 변환 로직: Entity -> DTO
     */
    private DiaryResponseDto.DiaryDetail convertToDetailDto(DiaryEntry entry) {
        return DiaryResponseDto.DiaryDetail.builder()
                .diaryId(entry.getDiaryId())
                .petId(entry.getPetId())
                .diagnosisId(entry.getDiagnosisId())
                .entryDate(entry.getEntryDate())
                .memo(entry.getMemo())
                // 정수 리스트(checkIds)를 DTO의 리스트로 즉시 변환 (참조 끊기 위해 새 리스트 생성)
                .checkList(new ArrayList<>(entry.getCheckIds()))
                .build();
    }
}