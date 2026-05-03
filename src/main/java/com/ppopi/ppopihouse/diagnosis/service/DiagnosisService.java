package com.ppopi.ppopihouse.diagnosis.service;

import com.ppopi.ppopihouse.diagnosis.domain.Diagnosis;
import com.ppopi.ppopihouse.diagnosis.domain.EyeSymptom;
import com.ppopi.ppopihouse.diagnosis.dto.external.AiDiagnosisRequest;
import com.ppopi.ppopihouse.diagnosis.dto.external.AiDiagnosisResponse;
import com.ppopi.ppopihouse.diagnosis.dto.external.ImageValidationResponse;
import com.ppopi.ppopihouse.diagnosis.dto.response.DiagnosisResponse;
import com.ppopi.ppopihouse.diagnosis.dto.response.RecentDiagnosisResponse;
import com.ppopi.ppopihouse.diagnosis.repository.DiagnosisRepository;
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

        List<String> symptoms = symptomIds.stream()
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

        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setPet(pet);
        diagnosis.setDiagnosisDate(LocalDate.now());
        diagnosis.setImageUrl(imageUrl);
        diagnosis.setTriageKey(aiResponse.getTriage());
        diagnosis.setTriageConfidence(aiResponse.getTriageConfidence());
        diagnosis.setGuideMsg(aiResponse.getGuidanceMessage());
        diagnosis.setGuideAction(aiResponse.getGuidanceAction());
        diagnosis.setGuideWarn(aiResponse.getGuidanceWarning());

        diagnosisRepository.save(diagnosis);

        return new DiagnosisResponse(
                aiResponse.getDisease(),
                aiResponse.getTriage(),
                aiResponse.getTriageConfidence(),
                aiResponse.getFamilyLabel(),
                aiResponse.getGuidanceMessage(),
                aiResponse.getGuidanceAction()
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
}