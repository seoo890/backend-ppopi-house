package com.ppopi.ppopihouse.diary.repository;

import com.ppopi.ppopihouse.diary.domain.DiaryEntry;
import com.ppopi.ppopihouse.diary.domain.DiaryEntryCheck;
import com.ppopi.ppopihouse.diary.domain.DiaryEntryCheckId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DiaryEntryCheckRepository extends JpaRepository<DiaryEntryCheck, DiaryEntryCheckId> {

    // 특정 다이어리에 속한 모든 체크 항목 조회
    List<DiaryEntryCheck> findAllByDiaryEntry(DiaryEntry diaryEntry);

    // 다이어리 수정/삭제 시 기존 매핑 데이터를 청소하기 위한 메서드
    void deleteByDiaryEntry(DiaryEntry diaryEntry);
}