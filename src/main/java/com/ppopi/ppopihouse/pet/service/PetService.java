package com.ppopi.ppopihouse.pet.service;

import com.ppopi.ppopihouse.diary.dto.DiaryDto;
import com.ppopi.ppopihouse.member.domain.Member;
import com.ppopi.ppopihouse.member.repository.MemberRepository;
import com.ppopi.ppopihouse.pet.domain.Pet;
import com.ppopi.ppopihouse.pet.dto.request.PetCreateRequest;
import com.ppopi.ppopihouse.pet.dto.response.PetCreateResponse;
import com.ppopi.ppopihouse.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PetService {

    private final PetRepository petRepository;
    private final MemberRepository memberRepository;
    /**
     * 반려동물 목록 조회
     */
    public List<DiaryDto.PetSummary> findPetSummaries(Long memberId) {
        int currentYear = LocalDate.now().getYear(); // 2026년 기준

        return petRepository.findAllByMember_MemberId(memberId).stream()
                .map(pet -> DiaryDto.PetSummary.builder()
                        .petId(pet.getPetId())
                        .name(pet.getName())
                        .species(pet.getSpecies() != null ? pet.getSpecies().toString() : null)
                        .breed(pet.getBreed())
                        .age(currentYear - pet.getBirthYear())
                        .sex(pet.getSex() != null ? pet.getSex().toString() : null)
                        .color(pet.getColor())
                        .build())
                .collect(Collectors.toList());
    }

    public List<String> getBreeds(String species) {
        return PetBreedProvider.getBreeds(species);
    }

    @Transactional
    public PetCreateResponse createPet(Long memberId, PetCreateRequest request) {
        validatePetCreateRequest(request);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        long petCount = petRepository.countByMember_MemberId(memberId);
        if (petCount >= 3) {
            throw new IllegalArgumentException("반려동물은 최대 3마리까지 등록할 수 있습니다.");
        }

        Pet pet = new Pet();
        pet.setMember(member);
        pet.setName(request.getName());
        pet.setSpecies(request.getSpecies());
        pet.setBreed(request.getBreed());
        pet.setBirthYear(calculateBirthYear(request.getAge()));
        pet.setSex(request.getSex());
        pet.setColor(request.getColor());

        Pet savedPet = petRepository.save(pet);

        return new PetCreateResponse(savedPet.getPetId());
    }

    private int calculateBirthYear(int age) {
        return LocalDate.now().getYear() - age;
    }

    private void validatePetCreateRequest(PetCreateRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("반려동물 이름은 필수입니다.");
        }

        if (request.getSpecies() == null || request.getSpecies().isBlank()) {
            throw new IllegalArgumentException("반려동물 종은 필수입니다.");
        }

        if (request.getBreed() == null || request.getBreed().isBlank()) {
            throw new IllegalArgumentException("품종은 필수입니다.");
        }

        if (request.getAge() < 0 || request.getAge() > 30) {
            throw new IllegalArgumentException("올바르지 않은 나이입니다.");
        }

        if (request.getSex() == null || request.getSex().isBlank()) {
            throw new IllegalArgumentException("성별은 필수입니다.");
        }
    }
}
