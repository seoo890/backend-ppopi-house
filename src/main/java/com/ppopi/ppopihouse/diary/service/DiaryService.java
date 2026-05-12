package com.ppopi.ppopihouse.diary.service;

import com.ppopi.ppopihouse.diagnosis.domain.Diagnosis;
import com.ppopi.ppopihouse.diagnosis.dto.response.DiagnosisResponse;
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

            // [피드백 반영] Diagnosis 엔티티를 DiagnosisResponse DTO로 변환
            DiagnosisResponse diagnosisResponse = convertToDiagnosisResponse(entry.getDiagnosis());

            return DiaryResponseDto.DayDetailResponse.builder()
                    .diaryId(entry.getDiaryId())
                    .petId(entry.getPet().getPetId())
                    .petName(entry.getPet().getName())
                    .entryDate(entry.getEntryDate())
                    .memo(entry.getMemo())
                    .diagnosis(diagnosisResponse) // 엔티티 대신 DTO 주입
                    .checkIds(checks.stream().map(c -> c.getCheckCode().getCheckId()).collect(Collectors.toList()))
                    .checkNames(checks.stream().map(c -> c.getCheckCode().getCheckName()).collect(Collectors.toList()))
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * 엔티티를 DTO로 변환하는 private 헬퍼 메서드
     */
    private DiagnosisResponse convertToDiagnosisResponse(Diagnosis diagnosis) {
        if (diagnosis == null) return null;

        return new DiagnosisResponse(
                diagnosis.getImageUrl(),
                formatStatus(diagnosis.getTriageKey()),
                diagnosis.getDisease().getDiseaseName(),
                formatAffectedArea(diagnosis.getDisease().getAffectedArea()),
                formatConfidence(diagnosis.getTriageConfidence()),
                diagnosis.getGuideAction(),
                diagnosis.getGuideMsg(),
                diagnosis.getGuideWarn()

        );
    }

    /**
     * 다이어리 작성을 위한 증상 체크리스트 전체 조회
     */
    public List<DiaryDto.CheckCodeResponse> findAllCheckCodes() {
        return checkCodeRepository.findAll().stream()
                .map(code -> new DiaryDto.CheckCodeResponse(code.getCheckId(), code.getCheckName()))
                .collect(Collectors.toList());
    }

    /**
     * 다이어리 생성
     */
    @Transactional
    public void saveDiary(Long memberId, DiaryDto.CreateRequest request) {
        // 1. 반려동물 존재 및 권한 검증
        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 반려동물입니다."));

        if (!pet.getMember().getMemberId().equals(memberId)) {
            throw new SecurityException("해당 반려동물에 대한 접근 권한이 없습니다.");
        }

        // 3. 다이어리 엔티티 생성 및 저장
        DiaryEntry entry = new DiaryEntry();
        entry.setPet(pet);
        entry.setDiagnosis(null);   // 직접 작성 다이어리는 진단 x
        entry.setEntryDate(LocalDate.now()); // 서버 현재 날짜 적용
        entry.setMemo(request.getMemo());

        diaryRepository.save(entry);

        // 4. 체크리스트 항목 저장
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

    private String formatStatus(String triage) {
        if (triage == null || triage.isBlank()) return "UNKNOWN";
        return triage.trim().toUpperCase();
    }

    private String formatAffectedArea(String area) {
        if (area == null) return null;

        return switch (area) {
            case "cornea_ulcerative", "cornea_nonulcerative" -> "각막";
            case "conjunctiva" -> "결막";
            case "eyelid" -> "눈꺼풀";
            case "lens_vitreous" -> "수정체/유리체";
            case "tear" -> "눈물";
            case "normal" -> "정상";
            default -> area;
        };
    }

    private int formatConfidence(double confidence) {
        return (int) Math.round(confidence * 100);
    }
}
