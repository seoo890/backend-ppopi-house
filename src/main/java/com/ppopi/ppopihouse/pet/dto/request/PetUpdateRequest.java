package com.ppopi.ppopihouse.pet.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 반려동물 정보 수정 요청 DTO
 */
@Getter
@Setter
public class PetUpdateRequest {
    private String name;
    private String species;
    private String breed;
    private int age;
    private String sex;
    private int color;
}