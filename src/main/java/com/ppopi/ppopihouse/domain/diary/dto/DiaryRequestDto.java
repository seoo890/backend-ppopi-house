package com.ppopi.ppopihouse.domain.diary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class DiaryRequestDto {

    /**
     * 다이어리 추가 요청 (펫ID, 메모, 체크리스트)
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Create {
        private Long petId;
        private Long diagnosisId; // 진단 아이디 (진단 후 생성 시 필요)
        private LocalDate entryDate; // 일자 (기본값 설정 가능)
        private String memo;
        private List<Integer> checkList;
    }

    /**
     * 특정 다이어리 수정 요청 (메모, 체크리스트)
     * 수정 시 diaryId는 보통 API 경로(Path Variable)로 받으므로 본문에서는 제외합니다.
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Update {
        private String memo;
        private List<Integer> checkList;
    }
}