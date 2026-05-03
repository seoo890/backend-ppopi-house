package com.ppopi.ppopihouse.pet.service;

import com.ppopi.ppopihouse.diary.dto.DiaryDto;
import com.ppopi.ppopihouse.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service // [필수] 스프링 빈으로 등록
@RequiredArgsConstructor // [오류 해결] final 필드에 대한 생성자를 자동으로 생성
@Transactional(readOnly = true)
public class PetService {
    private final PetRepository petRepository;
    /**
     * 반려동물 목록 조회
     */
    public List<DiaryDto.PetSummary> findPetSummaries(Long memberId) {

        return petRepository.findAllByMember_MemberId(memberId).stream()
                .map(pet -> DiaryDto.PetSummary.builder()
                        .petId(pet.getPetId())
                        .name(pet.getName())
                        .color(pet.getColor())
                        .build())
                .collect(Collectors.toList());
    }

}
