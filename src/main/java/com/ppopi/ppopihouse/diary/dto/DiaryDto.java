package com.ppopi.ppopihouse.diary.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

public class DiaryDto {

    @Getter @Builder
    @JsonPropertyOrder({ "petId", "name", "species", "breed", "age", "sex" })
    public static class PetSummary {
        private Long petId;
        private String name;
        private Integer color;
    }

    @Getter @Setter
    public static class CreateRequest {
        private Long petId;
        private Long diagnosisId; // 진단 기반 자동 생성 시 전달받을 ID (선택 사항)
        private String memo;
        private List<Long> checkIds;
    }

    @Getter @Setter
    public static class UpdateRequest {
        private Long diaryId;
        private String memo;
        private List<Long> checkIds;
    }

    @Getter @Builder
    public static class DetailResponse {
        private Long diaryId;
        private Long petId;
        private String petName;
        private LocalDate entryDate;
        private String memo;
        private List<Long> checkIds;
    }
}
