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
import com.ppopi.ppopihouse.global.infra.cloud.ImageStorageService;
import com.ppopi.ppopihouse.pet.domain.Pet;
import com.ppopi.ppopihouse.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiagnosisService {

    private final PetRepository petRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final EyeDiseaseCodeRepository eyeDiseaseCodeRepository;
    private final ImageValidationClient imageValidationClient;
    private final ImageStorageService imageStorageService;
    private final AiDiagnosisClient aiDiagnosisClient;

    public DiagnosisResponse diagnose(
            Long petId,
            MultipartFile image,
            List<Long> symptomIds
    ) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 반려동물입니다."));

        ImageValidationResponse validation = imageValidationClient.validate(image);

        if (validation == null || !validation.isValid()) {
            throw new IllegalArgumentException(
                    validation != null ? validation.getMessage() : "이미지 유효성 검사에 실패했습니다."
            );
        }

        String imageUrl = imageStorageService.upload(image);

        List<String> symptoms = symptomIds == null
                ? List.of()
                : symptomIds.stream()
                .map(EyeSymptom::fromId)
                .map(EyeSymptom::getDescription)
                .toList();

        AiDiagnosisRequest aiRequest = new AiDiagnosisRequest(
                imageUrl,
                pet.getSpecies(),
                pet.getBreed(),
                calculateAge(pet.getBirthYear()),
                pet.getSex(),
                symptoms
        );

        AiDiagnosisResponse aiResponse = aiDiagnosisClient.diagnose(aiRequest);

        String diseaseName = normalizeDiseaseName(aiResponse.getDisease());
        String species = normalizeSpecies(pet.getSpecies());

        EyeDiseaseCode disease = eyeDiseaseCodeRepository
                .findByDiseaseNameAndInputSpecies(diseaseName, species)
                .orElseThrow(() -> new IllegalArgumentException(
                        "등록되지 않은 질병 코드입니다. disease=" + diseaseName + ", species=" + species
                ));

        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setPet(pet);
        diagnosis.setDiagnosisDate(LocalDate.now());
        diagnosis.setImageUrl(imageUrl);
        diagnosis.setDisease(disease);

        diagnosis.setTriageKey(aiResponse.getTriage());
        diagnosis.setTriageConfidence(aiResponse.getTriageConfidence());

        diagnosis.setGuideMsg(aiResponse.getGuidanceMessage());
        diagnosis.setGuideAction(aiResponse.getGuidanceAction());
        diagnosis.setGuideWarn(aiResponse.getGuidanceWarning());

        diagnosisRepository.save(diagnosis);

        return new DiagnosisResponse(
                new DiagnosisResponse.Summary(
                        formatStatus(aiResponse.getTriage()),
                        formatDiseaseTitle(aiResponse.getDisease(), aiResponse.getFamilyLabel()),
                        formatConfidence(aiResponse.getTriageConfidence())
                ),
                new DiagnosisResponse.ResultCard(
                        formatDescriptionTitle(aiResponse.getDisease()),
                        aiResponse.getGuidanceMessage(),
                        formatGuideTitle(aiResponse.getTriage()),
                        aiResponse.getGuidanceAction()
                )
        );
    }

    public RecentDiagnosisResponse getTodayDiagnosis(Long memberId, Long petId, LocalDate date) {

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 반려동물입니다."));

        if (!pet.getMember().getMemberId().equals(memberId)) {
            throw new SecurityException("해당 반려동물에 대한 접근 권한이 없습니다.");
        }

        return diagnosisRepository
                .findTopByPet_PetIdAndDiagnosisDateOrderByDiagnosisIdDesc(petId, date)
                .map(RecentDiagnosisResponse::from)
                .orElseGet(RecentDiagnosisResponse::empty);
    }

    private int calculateAge(int birthYear) {
        return Year.now().getValue() - birthYear;
    }

    private String formatGuideTitle(String triage) {
        if (triage == null) return "진료 권장";

        return switch (triage.toLowerCase()) {
            case "normal" -> "정상 안구입니다";
            case "soon" -> "일주일 내 내원 권장";
            case "urgent" -> "24시간 이내 내원 권장";
            case "emergency" -> "즉시 응급 진료 권장";
            default -> "진료 권장";
        };
    }

    private String formatStatus(String triage) {
        if (triage == null) return "Unknown";

        return switch (triage.toLowerCase()) {
            case "normal" -> "Normal";
            case "soon" -> "Soon";
            case "urgent" -> "Urgent";
            case "emergency" -> "Emergency";
            default -> triage;
        };
    }

    private String formatDiseaseTitle(String diseaseName, String familyLabel) {
        if ("정상".equals(diseaseName)) {
            return "정상";
        }

        String area = switch (familyLabel) {
            case "cornea" -> "각막";
            case "conjunctiva" -> "결막";
            case "lens" -> "수정체";
            case "retina" -> "망막";
            default -> null;
        };

        return area == null ? diseaseName : diseaseName + " | " + area;
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

    private int formatConfidence(double confidence) {
        return (int) Math.round(confidence * 100);
    }

    private String formatDescriptionTitle(String diseaseName) {
        if ("정상".equals(diseaseName)) {
            return "정상 안구입니다";
        }

        return diseaseName + "이 의심됩니다";
    }
}