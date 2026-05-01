package com.ppopi.ppopihouse.domain.diary.repository;

import com.ppopi.ppopihouse.domain.diary.entity.DiaryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<DiaryEntry, Long> {

    /**
     * 특정 날짜의 모든 다이어리 항목을 조회합니다. (일간 요청용)
     */
    List<DiaryEntry> findAllByEntryDate(LocalDate entryDate);

    /**
     * 특정 기간(시작일 ~ 종료일) 사이의 모든 다이어리 항목을 조회합니다. (월간 요청용)
     * 이 데이터를 기반으로 Service 레이어에서 일자별 색깔 리스트를 가공합니다.
     */
    List<DiaryEntry> findAllByEntryDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * 특정 펫의 모든 다이어리 항목을 조회합니다.
     */
    List<DiaryEntry> findAllByPetId(Long petId);
    // 특정 펫의 날짜 범위 조회 (필터링용)
    List<DiaryEntry> findAllByPetIdAndEntryDateBetween(Long petId, LocalDate start, LocalDate end);

    // 특정 펫의 특정 날짜 조회 (필터링용)
    List<DiaryEntry> findAllByPetIdAndEntryDate(Long petId, LocalDate date);
}