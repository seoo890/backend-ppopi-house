package com.ppopi.ppopihouse.diary.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

public class DiaryDto {

    @Getter @Builder
    @JsonPropertyOrder({ "petId", "name", "species", "breed", "birthYear", "age", "sex", "color" })
    public static class PetSummary {
        private Long petId;
        private String name;
        private String species;
        private String breed;
        private Integer age;
        private String sex;
        private Integer color;
    }

    @Getter @Setter
    public static class CreateRequest {
        private Long petId;
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

    @Getter @Setter @Builder
    @NoArgsConstructor @AllArgsConstructor
    public static class CheckCodeResponse {
        private Long checkId;
        private String checkName;
    }

    @Getter @Builder @AllArgsConstructor
    public static class DiaryDetailResponse {
        private Long diaryId;
        private LocalDate entryDate;
        private String memo;
        private Long petId;
        private String petName;

        // [수정] 진단 정보와 증상을 포함하도록 구성
        private List<SymptomResponse> diagnosisSymptoms;
        private String diagnosisResult;
        private DiagnosisInfo diagnosisDetail; // 상세 객체가 필요할 경우를 대비
    }

    @Getter @Builder @AllArgsConstructor
    public static class DiagnosisInfo {
        private String imageUrl;
        private String diseaseName;
        private List<SymptomResponse> symptoms;
    }

    @Getter @AllArgsConstructor
    public static class SymptomResponse {
        private Long id;
        private String description;
    }
}