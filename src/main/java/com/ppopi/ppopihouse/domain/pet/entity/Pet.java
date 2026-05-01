package com.ppopi.ppopihouse.domain.pet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity // DB 테이블과 매핑되는 클래스임을 선언
@Getter
@NoArgsConstructor
@Table(name = "pet") // 실제 DB 테이블 이름
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long petId;

    private Long memberId;
    private String name;
    private String species;
    private String breed;
    private int birthYear;
    private char sex;
    private int color;

    // 생성자 (나중에 Service에서 사용)
    public Pet(Long memberId, String name, String species, String breed, int birthYear, char sex, int color) {
        this.memberId = memberId;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.birthYear = birthYear;
        this.sex = sex;
        this.color = color;
    }
}