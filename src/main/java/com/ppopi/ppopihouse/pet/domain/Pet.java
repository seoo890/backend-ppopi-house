package com.ppopi.ppopihouse.pet.domain;

import com.ppopi.ppopihouse.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pet")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor // 🌟 @Builder 사용 시 무조건 세트로 들어가야 하는 전산학적 정석 생성자
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pet_id")
    private Long petId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String species;

    @Column(nullable = false)
    private String breed;

    @Column(name = "birth_year", nullable = false)
    private int birthYear;

    @Column(nullable = false)
    private String sex;

    @Column(nullable = false)
    private int color;

    @Builder.Default // 🌟 컴파일 경고를 영구 해제하고 빌더 가동 시 초기값 false 주입을 강제하는 속성
    @Column(nullable = false)
    private boolean deleted = false;
}
