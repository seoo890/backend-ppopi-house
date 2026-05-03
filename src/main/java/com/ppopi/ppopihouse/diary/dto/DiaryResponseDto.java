package com.ppopi.ppopihouse.diary.dto;

import com.ppopi.ppopihouse.diagnosis.domain.Diagnosis;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.util.List;

public class DiaryResponseDto {

    @Getter @Builder
    public static class MonthlyResponse {

        private List<MonthlyRecord> diaryList;
    }

    @Getter @Builder
    public static class MonthlyRecord {

        private LocalDate date;
        private List<Integer> tags;
    }

    @Getter @Builder
    public static class DayDetailResponse {
        private Long diaryId;
        private Long petId;
        private String petName;
        private LocalDate entryDate;
        private String memo;
        private List<Long> checkIds;      // 체크리스트 ID 목록
        private List<String> checkNames;  // 체크리스트 이름 목록 (화면 표시용)
        private Diagnosis diagnosis;      // 진단 객체 전체
    }
}