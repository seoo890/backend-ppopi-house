package com.ppopi.ppopihouse.domain.pet.service;

import com.ppopi.ppopihouse.domain.pet.dto.PetRegisterRequestDto;
import com.ppopi.ppopihouse.domain.pet.entity.Pet;
import com.ppopi.ppopihouse.domain.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class PetService {

    private final PetRepository petRepository;

    public Long registerPet(PetRegisterRequestDto requestDto) {
        // 나이를 바탕으로 출생연도 계산 로직 (2026년 기준)
        int birthYear = LocalDate.now().getYear() - requestDto.getAge();

        Pet pet = new Pet(
                requestDto.getMemberId(),
                requestDto.getName(),
                requestDto.getSpecies(),
                requestDto.getBreed(),
                birthYear,
                requestDto.getGender(),
                requestDto.getColor()
        );

        return petRepository.save(pet).getPetId();
    }
}