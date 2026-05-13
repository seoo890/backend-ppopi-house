package com.ppopi.ppopihouse.pet.repository;

import com.ppopi.ppopihouse.member.domain.Member;
import com.ppopi.ppopihouse.pet.domain.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {

    long countByMember_MemberId(Long memberId);
    List<Pet> findAllByMember_MemberId(Long memberId);
    boolean existsByMemberAndDeletedFalse(Member member);
    List<Pet> findAllByMember_MemberIdAndDeletedFalseOrderByPetIdAsc(Long memberId);
    Optional<Pet> findByPetIdAndMember_MemberIdAndDeletedFalse(Long petId, Long memberId);
}