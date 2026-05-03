package com.ppopi.ppopihouse.diary.service;

import com.ppopi.ppopihouse.diagnosis.domain.Diagnosis;
import com.ppopi.ppopihouse.diagnosis.repository.DiagnosisRepository;
import com.ppopi.ppopihouse.diary.domain.*;
import com.ppopi.ppopihouse.diary.dto.DiaryDto;
import com.ppopi.ppopihouse.diary.dto.DiaryResponseDto;
import com.ppopi.ppopihouse.diary.repository.*;
import com.ppopi.ppopihouse.pet.domain.Pet;
import com.ppopi.ppopihouse.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final DiaryEntryCheckRepository entryCheckRepository;
    private final DiaryCheckCodeRepository checkCodeRepository;
    private final PetRepository petRepository;
    private final DiagnosisRepository diagnosisRepository;

    /**
     * 월별 다이어리 기록 조회
     */
    public DiaryResponseDto.MonthlyResponse findMonthlyRecords(Long memberId, int year, int month) {
        List<Pet> myPets = petRepository.findAllByMember_MemberId(memberId);

        LocalDate start = YearMonth.of(year, month).atDay(1);
        LocalDate end = YearMonth.of(year, month).atEndOfMonth();

        List<DiaryEntry> entries = diaryRepository.findAllByPetInAndEntryDateBetween(myPets, start, end);

        List<DiaryResponseDto.MonthlyRecord> recordList = entries.stream()
                .collect(Collectors.groupingBy(DiaryEntry::getEntryDate))
                .entrySet().stream()
                .map(entry -> DiaryResponseDto.MonthlyRecord.builder()
                        .date(entry.getKey())
                        .tags(entry.getValue().stream()
                                .map(d -> d.getPet().getColor())
                                .distinct()
                                .sorted()
                                .collect(Collectors.toList()))
                        .build())
                .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                .collect(Collectors.toList());

        return DiaryResponseDto.MonthlyResponse.builder()
                .diaryList(recordList)
                .build();
    }

    /**
     * 일별 다이어리 상세 조회
     */
    public List<DiaryResponseDto.DayDetailResponse> findDailyDiaries(Long memberId, int year, int month, int day) {
        LocalDate targetDate = LocalDate.of(year, month, day);
        List<Pet> myPets = petRepository.findAllByMember_MemberId(memberId);
        List<DiaryEntry> entries = diaryRepository.findAllByPetInAndEntryDate(myPets, targetDate);

        return entries.stream().map(entry -> {
            List<DiaryEntryCheck> checks = entryCheckRepository.findAllByDiaryEntry(entry);
            return DiaryResponseDto.DayDetailResponse.builder()
                    .diaryId(entry.getDiaryId())
                    .petId(entry.getPet().getPetId())
                    .petName(entry.getPet().getName())
                    .entryDate(entry.getEntryDate())
                    .memo(entry.getMemo())
                    .diagnosis(entry.getDiagnosis())
                    .checkIds(checks.stream().map(c -> c.getCheckCode().getCheckId()).collect(Collectors.toList()))
                    .checkNames(checks.stream().map(c -> c.getCheckCode().getCheckName()).collect(Collectors.toList()))
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * 다이어리 생성
     */
    @Transactional
    public void saveDiary(Long memberId, DiaryDto.CreateRequest request) {
        // 1. 소유권 검증: 요청된 petId가 현재 로그인한 유저의 것인지 확인
        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 반려동물입니다."));

        if (!pet.getMember().getMemberId().equals(memberId)) {
            throw new SecurityException("해당 반려동물에 대한 접근 권한이 없습니다.");
        }

        // 2. 진단 정보 조회 (있는 경우)
        Diagnosis diagnosis = null;
        if (request.getDiagnosisId() != null) {
            diagnosis = diagnosisRepository.findById(request.getDiagnosisId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 진단 기록입니다."));
        }

        // 3. 다이어리 본체 저장
        DiaryEntry entry = new DiaryEntry();
        entry.setPet(pet);
        entry.setDiagnosis(diagnosis);
        entry.setEntryDate(request.getEntryDate());
        entry.setMemo(request.getMemo());

        diaryRepository.save(entry);

        // 4. 체크리스트 저장
        if (request.getCheckIds() != null && !request.getCheckIds().isEmpty()) {
            saveAllChecks(entry, request.getCheckIds());
        }
    }

    /**
     * 다이어리 수정
     */
    @Transactional
    public void updateDiary(Long memberId, Long diaryId, DiaryDto.UpdateRequest request) {
        DiaryEntry entry = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 기록을 찾을 수 없습니다."));

        // 소유권 검증: 다이어리의 주인이 현재 사용자 인지 확인
        if (!entry.getPet().getMember().getMemberId().equals(memberId)) {
            throw new SecurityException("수정 권한이 없습니다.");
        }

        entry.setMemo(request.getMemo());

        entryCheckRepository.deleteByDiaryEntry(entry);
        if (request.getCheckIds() != null) {
            saveAllChecks(entry, request.getCheckIds());
        }
    }

    /**
     * 다이어리 삭제*/
    @Transactional
    public void deleteDiary(Long memberId, Long diaryId) {
        DiaryEntry entry = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 기록을 찾을 수 없습니다."));

        if (!entry.getPet().getMember().getMemberId().equals(memberId)) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }

        entryCheckRepository.deleteByDiaryEntry(entry);
        diaryRepository.delete(entry);
    }

    /**
     * 반려동물 목록 조회
     */
    public List<DiaryDto.PetSummary> findPetSummaries(Long memberId) {
        return petRepository.findAllByMember_MemberId(memberId).stream()
                .map(pet -> DiaryDto.PetSummary.builder()
                        .petId(pet.getPetId())
                        .name(pet.getName())
                        .species(pet.getSpecies() != null ? pet.getSpecies().toString() : null)
                        .breed(pet.getBreed())
                        .age(2026 - pet.getBirthYear())
                        .sex(pet.getSex() != null ? pet.getSex().toString() : null)
                        .build())
                .collect(Collectors.toList());
    }

    private void saveAllChecks(DiaryEntry entry, List<Long> checkIds) {
        List<DiaryEntryCheck> checks = checkIds.stream().map(checkId -> {
            DiaryCheckCode code = checkCodeRepository.findById(checkId)
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 체크 항목 ID: " + checkId));

            DiaryEntryCheckId id = new DiaryEntryCheckId();
            id.setDiaryId(entry.getDiaryId());
            id.setCheckId(code.getCheckId());

            DiaryEntryCheck mapping = new DiaryEntryCheck();
            mapping.setId(id);
            mapping.setDiaryEntry(entry);
            mapping.setCheckCode(code);
            return mapping;
        }).collect(Collectors.toList());

        entryCheckRepository.saveAll(checks);
    }
}