package com.ppopi.ppopihouse.domain.pet.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PetRegisterRequestDto {
    private String name;
    private String species;
    private String breed;
    private char gender;
    private int age;
    private int color;
    private Long memberId; // 현재 로그인된 회원 정보가 넘어온다고 가정
}