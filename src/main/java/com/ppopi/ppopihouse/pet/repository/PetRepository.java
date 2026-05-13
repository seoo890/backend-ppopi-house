package com.ppopi.ppopihouse.pet.repository;

import com.ppopi.ppopihouse.member.domain.Member;
import com.ppopi.ppopihouse.pet.domain.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {

    boolean existsByMember(Member member);
    long countByMember_MemberId(Long memberId);
    List<Pet> findAllByMember_MemberId(Long memberId);
    void deleteByMember(Member member);
    boolean existsByMemberAndDeletedFalse(Member member);
    List<Pet> findAllByMemberAndDeletedFalse(Member member);
}