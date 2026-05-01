package com.ppopi.ppopihouse.domain.pet.repository;

// 1. 컬렉션 프레임워크의 List 인터페이스 (필수)
import java.util.List;

// 2. 관리 대상인 Pet 엔티티 (필수)
import com.ppopi.ppopihouse.domain.pet.entity.Pet;

// 3. 스프링 데이터 JPA 관련 (이미 존재할 가능성이 높음)
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    // 기본 CRUD는 JpaRepository가 다 알아서 해줍니다.
    List<Pet> findAllByMemberId(Long memberId);
}