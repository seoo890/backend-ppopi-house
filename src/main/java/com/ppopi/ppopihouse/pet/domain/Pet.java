package com.ppopi.ppopihouse.pet.domain;

import com.ppopi.ppopihouse.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "pet")
@Getter
@Setter
@Builder
@NoArgsConstructor  // 🌟 중복 선언 제거 및 기본 생성자 정렬
@AllArgsConstructor // 🌟 빌더 패턴 스펙 호환을 위한 생성자 단일 선언
// 🌟 JPA 레포지토리의 delete 호출 시 실행될 소프트 딜리트 가로채기 자동화 쿼리
@SQLDelete(sql = "UPDATE pet SET deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE pet_id = ?")
// 🌟 모든 일반 조회(find) 쿼리 발생 시 삭제되지 않은 데이터만 영속성 컨텍스트에 바인딩하는 전역 필터
@SQLRestriction("deleted = false")
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

    @Builder.Default
    @Column(nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * 비즈니스 로직 레이어에서 특정 시점의 타임스탬프를 직접 주입하여
     * 더티 체킹(Dirty Checking) 기반으로 소프트 딜리트를 수행할 때 사용하는 도메인 메서드
     */
    public void delete(LocalDateTime deletedAt) {
        this.deleted = true;
        this.deletedAt = deletedAt;
    }

    /**
     * 소프트 딜리트 처리된 반려동물 데이터를
     * 다시 활성화 상태로 복구하기 위한 도메인 메서드
     */
    public void restore() {
        this.deleted = false;
        this.deletedAt = null;
    }
}