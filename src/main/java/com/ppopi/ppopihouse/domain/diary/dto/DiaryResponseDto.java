package com.ppopi.ppopihouse.domain.diary.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class DiaryResponseDto {



    /**
     * 펫 리스트 응답용 (펫 아이디, 이름, 색깔)
     */
    @Getter
    @Builder
    public static class MonthlyRecord {
        private LocalDate date;
        private List<PetSummary> pets; // Integer 대신 객체 리스트로 변경
    }

    @Getter
    @Builder
    public static class PetSummary {
        private Long petId;
        private String name;
        private int color;
    }

    /**
     * 일별 다이어리 리스트 및 상세 응답용
     */
    @Getter
    @Builder
    public static class DiaryDetail {
        private Long diaryId;
        private Long petId;
        private Long diagnosisId;
        private LocalDate entryDate;
        private String memo;
        private List<Integer> checkList; // 체크리스트 항목들
    }

}