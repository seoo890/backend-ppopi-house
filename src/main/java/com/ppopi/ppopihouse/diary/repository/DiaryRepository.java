package com.ppopi.ppopihouse.diary.repository;

import com.ppopi.ppopihouse.diary.domain.DiaryEntry;
import com.ppopi.ppopihouse.pet.domain.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface DiaryRepository extends JpaRepository<DiaryEntry, Long> {
    // 특정 펫 리스트의 기간 내 다이어리 조회
    List<DiaryEntry> findAllByPetInAndEntryDateBetween(List<Pet> pets, LocalDate start, LocalDate end);

    // 일별 조회를 위한 메서드 추가
    List<DiaryEntry> findAllByPetInAndEntryDate(List<Pet> pets, LocalDate entryDate);
}