package com.ppopi.ppopihouse.pet.repository;

import com.ppopi.ppopihouse.member.domain.Member;
import com.ppopi.ppopihouse.pet.domain.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {

    boolean existsByMember(Member member);
}