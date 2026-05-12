package com.ppopi.ppopihouse.member.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "member")
@Getter
@Setter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "kakao_user_id", nullable = false, unique = true)
    private String kakaoUserId;

    @Column(nullable = false)
    private boolean deleted = false;

    private LocalDateTime deletedAt;

    public void withdraw(LocalDateTime deletedAt) {
        this.deleted = true;
        this.deletedAt = deletedAt;
    }
}