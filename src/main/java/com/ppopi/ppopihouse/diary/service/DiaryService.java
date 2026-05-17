package com.ppopi.ppopihouse.diary.service;

import com.ppopi.ppopihouse.diagnosis.domain.Diagnosis;
import com.ppopi.ppopihouse.diagnosis.domain.EyeSymptom;
import com.ppopi.ppopihouse.diagnosis.dto.response.DiagnosisResponse;
import com.ppopi.ppopihouse.diagnosis.repository.DiagnosisRepository;
import com.ppopi.ppopihouse.diary.domain.*;
import com.ppopi.ppopihouse.diary.dto.DiaryDto;
import com.ppopi.ppopihouse.diary.dto.DiaryResponseDto;
import com.ppopi.ppopihouse.diary.repository.*;
import com.ppopi.ppopihouse.pet.domain.Pet;
import com.ppopi.ppopihouse.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
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
        List<Pet> myPets = petRepository.findAllByMember_MemberIdAndDeletedFalseOrderByPetIdAsc(memberId);
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
     * 일별 다이어리 상세 조회 (목록용)
     */
    public List<DiaryResponseDto.DayDetailResponse> findDailyDiaries(Long memberId, int year, int month, int day) {
        LocalDate targetDate = LocalDate.of(year, month, day);
        List<Pet> myPets = petRepository.findAllByMember_MemberIdAndDeletedFalseOrderByPetIdAsc(memberId);
        List<DiaryEntry> entries = diaryRepository.findAllByPetInAndEntryDate(myPets, targetDate);

        return entries.stream().map(entry -> {
            List<DiaryEntryCheck> checks = entryCheckRepository.findAllByDiaryEntry(entry);
            DiagnosisResponse diagnosisResponse = convertToDiagnosisResponse(entry.getDiagnosis());

            return DiaryResponseDto.DayDetailResponse.builder()
                    .diaryId(entry.getDiaryId())
                    .petId(entry.getPet().getPetId())
                    .petName(entry.getPet().getName())
                    .entryDate(entry.getEntryDate())
                    .memo(entry.getMemo())
                    .diagnosis(diagnosisResponse)
                    .checkIds(checks.stream().map(c -> c.getCheckCode().getCheckId()).collect(Collectors.toList()))
                    .checkNames(checks.stream().map(c -> c.getCheckCode().getCheckName()).collect(Collectors.toList()))
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * 특정 다이어리 상세 조회 (단일용)
     */
    @Transactional(readOnly = true)
    public DiaryDto.DiaryDetailResponse getDiaryDetail(Long memberId, Long diaryId) {
        DiaryEntry entry = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 다이어리입니다."));

        if (!entry.getPet().getMember().getMemberId().equals(memberId)) {
            throw new SecurityException("접근 권한이 없습니다.");
        }

        // 진단 증상 파싱 로직 적용
        List<DiaryDto.SymptomResponse> symptomResponses = parseSymptomIds(
                entry.getDiagnosis() != null ? entry.getDiagnosis().getSymptomIds() : null
        );

        return DiaryDto.DiaryDetailResponse.builder()
                .diaryId(entry.getDiaryId())
                .entryDate(entry.getEntryDate())
                .memo(entry.getMemo())
                .petId(entry.getPet().getPetId())
                .petName(entry.getPet().getName())
                .diagnosisSymptoms(symptomResponses)
                .diagnosisResult(entry.getDiagnosis() != null ? entry.getDiagnosis().getGuideMsg() : null)
                .build();
    }

    /**
     * [핵심] symptom_ids ("1,3")를 List<SymptomResponse>로 변환하는 헬퍼 메서드
     */
    private List<DiaryDto.SymptomResponse> parseSymptomIds(String rawSymptomIds) {
        if (rawSymptomIds == null || rawSymptomIds.isBlank()) {
            return new ArrayList<>();
        }

        String cleanedIds = rawSymptomIds.replace("\"", "").trim();
        return Arrays.stream(cleanedIds.split(","))
                .map(String::trim)
                .filter(idStr -> !idStr.isEmpty())
                .map(idStr -> {
                    try {
                        EyeSymptom symptom = EyeSymptom.fromId(Long.parseLong(idStr));
                        return new DiaryDto.SymptomResponse(symptom.getId(), symptom.getDescription());
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(s -> s != null)
                .collect(Collectors.toList());
    }

    /**
     * 다이어리 작성을 위한 일반 건강 체크리스트 전체 조회 (DB 기반)
     */
    public List<DiaryDto.CheckCodeResponse> findAllCheckCodes() {
        // 1. 기존 EyeSymptom.values() 스트림 로직 전면 제거
        // 2. DB에서 마스터 데이터를 조회하여 DTO로 매핑
        return checkCodeRepository.findAll().stream()
                .map(code -> DiaryDto.CheckCodeResponse.builder()
                        .checkId(code.getCheckId())
                        .checkName(code.getCheckName())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveDiary(Long memberId, DiaryDto.CreateRequest request) {
        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 반려동물입니다."));

        if (!pet.getMember().getMemberId().equals(memberId)) {
            throw new AccessDeniedException("해당 반려동물에 대한 접근 권한이 없습니다.");
        }

        DiaryEntry entry = new DiaryEntry();
        entry.setPet(pet);
        entry.setEntryDate(LocalDate.now());
        entry.setMemo(request.getMemo());
        diaryRepository.save(entry);

        if (request.getCheckIds() != null && !request.getCheckIds().isEmpty()) {
            saveAllChecks(entry, request.getCheckIds());
        }
    }

    @Transactional
    public void updateDiary(Long memberId, Long diaryId, DiaryDto.UpdateRequest request) {
        DiaryEntry entry = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new NoSuchElementException("해당 기록을 찾을 수 없습니다."));

        if (!entry.getPet().getMember().getMemberId().equals(memberId)) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        entry.setMemo(request.getMemo());
        entryCheckRepository.deleteByDiaryEntry(entry);
        if (request.getCheckIds() != null) {
            saveAllChecks(entry, request.getCheckIds());
        }
    }

    @Transactional
    public void deleteDiary(Long memberId, Long diaryId) {
        DiaryEntry entry = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new NoSuchElementException("해당 기록을 찾을 수 없습니다."));

        if (!entry.getPet().getMember().getMemberId().equals(memberId)) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        entryCheckRepository.deleteByDiaryEntry(entry);
        diaryRepository.delete(entry);
    }

    private void saveAllChecks(DiaryEntry entry, List<Long> checkIds) {
        List<DiaryEntryCheck> checks = checkIds.stream().map(checkId -> {
            DiaryCheckCode code = checkCodeRepository.findById(checkId)
                    .orElseThrow(() -> new NoSuchElementException("유효하지 않은 체크 항목 ID: " + checkId));

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

    /**
     * 엔티티를 DTO로 변환 (증상 리스트 포함 버전)
     */
    private DiagnosisResponse convertToDiagnosisResponse(Diagnosis diagnosis) {
        if (diagnosis == null) return null;

        // 1. DB의 "1,3" 문자열을 파싱하여 List<SymptomResponse>로 변환
        List<DiaryDto.SymptomResponse> parsedSymptoms = parseSymptomIds(diagnosis.getSymptomIds());

        // 2. 수정된 DiagnosisResponse 생성자에 맞게 인자 전달
        return new DiagnosisResponse(
                diagnosis.getImageUrl(),
                formatStatus(diagnosis.getTriageKey()),
                diagnosis.getDisease().getDiseaseName(),
                formatAffectedArea(diagnosis.getDisease().getAffectedArea()),
                formatConfidence(diagnosis.getTriageConfidence()),
                diagnosis.getGuideAction(),
                diagnosis.getGuideMsg(),
                diagnosis.getGuideWarn(),
                parsedSymptoms // [추가된 인자]
        );
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