package com.ppopi.ppopihouse.domain.pet.repository;

import com.ppopi.ppopihouse.domain.pet.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    // 기본 CRUD는 JpaRepository가 다 알아서 해줍니다.
}