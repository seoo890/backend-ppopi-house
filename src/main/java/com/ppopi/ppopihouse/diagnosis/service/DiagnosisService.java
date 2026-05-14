package com.ppopi.ppopihouse.diagnosis.service;

import com.ppopi.ppopihouse.diagnosis.domain.Diagnosis;
import com.ppopi.ppopihouse.diagnosis.domain.EyeDiseaseCode;
import com.ppopi.ppopihouse.diagnosis.domain.EyeSymptom;
import com.ppopi.ppopihouse.diagnosis.dto.external.AiDiagnosisRequest;
import com.ppopi.ppopihouse.diagnosis.dto.external.AiDiagnosisResponse;
import com.ppopi.ppopihouse.diagnosis.dto.external.ImageValidationResponse;
import com.ppopi.ppopihouse.diagnosis.dto.response.DiagnosisResponse;
import com.ppopi.ppopihouse.diagnosis.dto.response.RecentDiagnosisResponse;
import com.ppopi.ppopihouse.diagnosis.repository.DiagnosisRepository;
import com.ppopi.ppopihouse.diagnosis.repository.EyeDiseaseCodeRepository;
import com.ppopi.ppopihouse.diary.domain.DiaryEntry;
import com.ppopi.ppopihouse.diary.repository.DiaryRepository;
import com.ppopi.ppopihouse.global.infra.cloud.ImageStorageService;
import com.ppopi.ppopihouse.pet.domain.Pet;
import com.ppopi.ppopihouse.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiagnosisService {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    private final PetRepository petRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final DiaryRepository diaryRepository;
    private final EyeDiseaseCodeRepository eyeDiseaseCodeRepository;
    private final ImageValidationClient imageValidationClient;
    private final ImageStorageService imageStorageService;
    private final AiDiagnosisClient aiDiagnosisClient;

    @Transactional
    public DiagnosisResponse diagnose(
            Long memberId,
            Long petId,
            MultipartFile image,
            List<Long> symptomIds
    ) {
        Pet pet = findValidatedPet(memberId, petId);

        validateImage(image);

        String imageUrl = imageStorageService.upload(image);

        List<String> symptoms = convertSymptomIdsToDescriptions(symptomIds);

        AiDiagnosisResponse aiResponse = requestAiDiagnosis(pet, imageUrl, symptoms);

        EyeDiseaseCode disease = findDiseaseCode(pet, aiResponse);

        Diagnosis savedDiagnosis = saveDiagnosis(
                pet,
                imageUrl,
                symptomIds,
                aiResponse,
                disease
        );

        createDiaryFromDiagnosis(pet, savedDiagnosis);

        return toDiagnosisResponse(imageUrl, aiResponse, disease);
    }

    public RecentDiagnosisResponse getTodayDiagnosis(Long memberId, Long petId, LocalDate date) {
        Pet pet = findValidatedPet(memberId, petId);

        return diagnosisRepository
                .findTopByPet_PetIdAndDiagnosisDateOrderByDiagnosisIdDesc(
                        pet.getPetId(),
                        date
                )
                .map(diagnosis -> {
                    List<Long> checkedIds = parseSymptomIds(diagnosis.getSymptomIds());
                    List<RecentDiagnosisResponse.SymptomChecklist> symptoms =
                            buildSymptomChecklist(checkedIds);

                    return toRecentDiagnosisResponse(diagnosis, symptoms);
                })
                .orElseGet(() ->
                        RecentDiagnosisResponse.empty(buildSymptomChecklist(List.of()))
                );
    }

    private Pet findPet(Long petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 반려동물입니다."));
    }

    private Pet findValidatedPet(Long memberId, Long petId) {
        Pet pet = findPet(petId);

        if (!pet.getMember().getMemberId().equals(memberId)) {
            throw new AccessDeniedException("해당 반려동물에 대한 접근 권한이 없습니다.");
        }

        return pet;
    }

    private void validateImage(MultipartFile image) {
        ImageValidationResponse validation = imageValidationClient.validate(image);

        if (validation == null || !validation.isValid()) {
            throw new IllegalArgumentException(
                    validation != null
                            ? validation.getMessage()
                            : "이미지 유효성 검사에 실패했습니다."
            );
        }
    }

    private List<String> convertSymptomIdsToDescriptions(List<Long> symptomIds) {
        if (symptomIds == null) {
            return List.of();
        }

        return symptomIds.stream()
                .map(EyeSymptom::fromId)
                .map(EyeSymptom::getDescription)
                .toList();
    }

    private AiDiagnosisResponse requestAiDiagnosis(
            Pet pet,
            String imageUrl,
            List<String> symptoms
    ) {
        AiDiagnosisRequest aiRequest = new AiDiagnosisRequest(
                imageUrl,
                pet.getSpecies(),
                pet.getBreed(),
                calculateAge(pet.getBirthYear()),
                pet.getSex(),
                symptoms
        );

        return aiDiagnosisClient.diagnose(aiRequest);
    }

    private EyeDiseaseCode findDiseaseCode(Pet pet, AiDiagnosisResponse aiResponse) {
        String diseaseName = normalizeDiseaseName(aiResponse.getDisease());
        String species = normalizeSpecies(pet.getSpecies());

        if ("정상".equals(diseaseName)) {
            return eyeDiseaseCodeRepository
                    .findByDiseaseNameAndInputSpecies(
                            diseaseName,
                            species
                    )
                    .orElseThrow(() -> new NoSuchElementException(
                            "등록되지 않은 정상 질병 코드입니다."
                    ));
        }

        String affectedArea =
                normalizeAffectedArea(aiResponse.getFamilyLabel(), diseaseName);

        return eyeDiseaseCodeRepository
                .findByDiseaseNameAndInputSpeciesAndAffectedArea(
                        diseaseName,
                        species,
                        affectedArea
                )
                .orElseThrow(() -> new NoSuchElementException(
                        "등록되지 않은 질병 코드입니다."
                ));
    }

    private Diagnosis saveDiagnosis(
            Pet pet,
            String imageUrl,
            List<Long> symptomIds,
            AiDiagnosisResponse aiResponse,
            EyeDiseaseCode disease
    ) {
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setPet(pet);
        diagnosis.setDiagnosisDate(LocalDate.now(SEOUL_ZONE));
        diagnosis.setImageUrl(imageUrl);
        diagnosis.setDisease(disease);
        diagnosis.setSymptomIds(convertSymptomIdsToString(symptomIds));
        diagnosis.setTriageKey(aiResponse.getTriage());
        diagnosis.setTriageConfidence(aiResponse.getTriageConfidence());
        diagnosis.setGuideMsg(aiResponse.getGuidanceMessage());
        diagnosis.setGuideAction(aiResponse.getGuidanceAction());
        diagnosis.setGuideWarn(aiResponse.getGuidanceWarning());

        return diagnosisRepository.save(diagnosis);
    }

    private void createDiaryFromDiagnosis(Pet pet, Diagnosis diagnosis) {
        LocalDate today = LocalDate.now(SEOUL_ZONE);

        DiaryEntry diaryEntry = diaryRepository
                .findByPet_PetIdAndEntryDate(pet.getPetId(), today)
                .orElseGet(() -> {
                    DiaryEntry newDiaryEntry = new DiaryEntry();
                    newDiaryEntry.setPet(pet);
                    newDiaryEntry.setEntryDate(today);
                    newDiaryEntry.setMemo(null);
                    return newDiaryEntry;
                });

        diaryEntry.setDiagnosis(diagnosis);

        diaryRepository.save(diaryEntry);
    }

    private DiagnosisResponse toDiagnosisResponse(
            String imageUrl,
            AiDiagnosisResponse aiResponse,
            EyeDiseaseCode disease
    ) {
        return new DiagnosisResponse(
                imageUrl,
                formatStatus(aiResponse.getTriage()),
                disease.getDiseaseName(),
                formatAffectedArea(disease.getAffectedArea()),
                formatConfidence(aiResponse.getTriageConfidence()),
                aiResponse.getGuidanceAction(),
                aiResponse.getGuidanceMessage(),
                aiResponse.getGuidanceWarning()
        );
    }

    private String convertSymptomIdsToString(List<Long> symptomIds) {
        if (symptomIds == null || symptomIds.isEmpty()) {
            return "";
        }

        return symptomIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    private int calculateAge(int birthYear) {
        return Year.now(SEOUL_ZONE).getValue() - birthYear;
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

    private String formatStatus(String triage) {
        if (triage == null || triage.isBlank()) return "UNKNOWN";
        return triage.trim().toUpperCase();
    }

    private String normalizeDiseaseName(String diseaseName) {
        if (diseaseName == null || diseaseName.isBlank()) {
            throw new IllegalArgumentException("AI 질병명이 비어 있습니다.");
        }

        return switch (diseaseName.toLowerCase()) {
            case "normal" -> "정상";
            default -> diseaseName.trim();
        };
    }

    private String normalizeSpecies(String species) {
        if (species == null || species.isBlank()) {
            throw new IllegalArgumentException("반려동물 종 정보가 비어 있습니다.");
        }

        return switch (species.toLowerCase()) {
            case "dog", "강아지", "개" -> "DOG";
            case "cat", "고양이" -> "CAT";
            default -> species.toUpperCase();
        };
    }

    private String normalizeAffectedArea(String affectedArea, String diseaseName) {
        boolean isNormal = "정상".equals(diseaseName);

        if (affectedArea == null || affectedArea.isBlank()) {
            if (isNormal) {
                return null;
            }

            throw new IllegalArgumentException("AI affectedArea 값이 비어 있습니다.");
        }

        return affectedArea.trim();
    }

    private int formatConfidence(double confidence) {
        return (int) Math.round(confidence * 100);
    }

    private RecentDiagnosisResponse toRecentDiagnosisResponse(
            Diagnosis diagnosis,
            List<RecentDiagnosisResponse.SymptomChecklist> symptoms
    ) {
        return new RecentDiagnosisResponse(
                true,
                diagnosis.getImageUrl(),
                formatStatus(diagnosis.getTriageKey()),
                diagnosis.getDisease().getDiseaseName(),
                formatAffectedArea(diagnosis.getDisease().getAffectedArea()),
                formatConfidence(diagnosis.getTriageConfidence()),
                diagnosis.getGuideAction(),
                diagnosis.getGuideMsg(),
                diagnosis.getGuideWarn(),
                symptoms
        );
    }

    private List<RecentDiagnosisResponse.SymptomChecklist> buildSymptomChecklist(
            List<Long> checkedIds
    ) {
        return Arrays.stream(EyeSymptom.values())
                .filter(symptom -> checkedIds.contains(symptom.getId()))
                .map(symptom -> new RecentDiagnosisResponse.SymptomChecklist(
                        symptom.getId(),
                        symptom.getDescription()
                ))
                .toList();
    }

    private List<Long> parseSymptomIds(String symptomIds) {
        if (symptomIds == null || symptomIds.isBlank()) {
            return List.of();
        }

        return Arrays.stream(symptomIds.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(Long::valueOf)
                .toList();
    }
}