package com.ppopi.ppopihouse.pet.service;

import com.ppopi.ppopihouse.member.domain.Member;
import com.ppopi.ppopihouse.member.repository.MemberRepository;
import com.ppopi.ppopihouse.pet.domain.Pet;
import com.ppopi.ppopihouse.pet.dto.request.PetCreateRequest;
import com.ppopi.ppopihouse.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PetService {

    private final PetRepository petRepository;
    private final MemberRepository memberRepository;

    public List<String> getBreeds(String species) {
        return PetBreedProvider.getBreeds(species);
    }

    @Transactional
    public void createPet(Long memberId, PetCreateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Pet pet = new Pet();
        pet.setMember(member);
        pet.setName(request.getName());
        pet.setSpecies(request.getSpecies());
        pet.setBreed(request.getBreed());
        pet.setBirthYear(request.getBirthYear());
        pet.setSex(request.getSex());
        pet.setColor(request.getColor());

        petRepository.save(pet);
    }
}