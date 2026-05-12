package com.ppopi.ppopihouse.pet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 반려동물 상세 정보 응답 DTO (수정 화면 진입 시 사용)
 */
@Getter
@Builder
@AllArgsConstructor
public class PetDetailResponse {
    private Long petId;
    private String name;
    private String species;
    private String breed;
    private int birthYear;
    private int age;
    private String sex;
    private int color;
}