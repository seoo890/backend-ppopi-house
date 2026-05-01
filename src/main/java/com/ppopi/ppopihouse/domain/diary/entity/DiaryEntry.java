package com.ppopi.ppopihouse.domain.diary.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자 보안 강화
@Table(name = "diary_entry")
public class DiaryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long diaryId;

    private Long petId;

    private Long diagnosisId; // AI 진단 결과와 연동

    private LocalDate entryDate;

    @Column(columnDefinition = "TEXT")
    private String memo;

    /**
     * 체크리스트 항목들
     * final을 붙여 참조가 바뀌지 않도록 설정 (노란 줄 해결)
     */
    @ElementCollection
    @CollectionTable(name = "diary_entry_check", joinColumns = @JoinColumn(name = "diary_id"))
    @Column(name = "check_id")
    private final List<Integer> checkIds = new ArrayList<>();

    @Builder
    public DiaryEntry(Long petId, Long diagnosisId, LocalDate entryDate, String memo, List<Integer> checkIds) {
        this.petId = petId;
        this.diagnosisId = diagnosisId;
        this.entryDate = entryDate;
        this.memo = memo;
        if (checkIds != null) {
            this.checkIds.addAll(checkIds);
        }
    }

    /**
     * 다이어리 수정 로직 (Setter 대신 도메인 메서드 사용)
     */
    public void updateDiary(String memo, List<Integer> checkIds) {
        this.memo = memo;
        // 컬렉션은 주소를 바꾸는 것(this.checkIds = checkIds)보다 내용을 비우고 채우는 것이 안전함
        this.checkIds.clear();
        if (checkIds != null) {
            this.checkIds.addAll(checkIds);
        }
    }
}