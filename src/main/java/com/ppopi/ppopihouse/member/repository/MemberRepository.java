package com.ppopi.ppopihouse.member.repository;

import com.ppopi.ppopihouse.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByKakaoUserId(String kakaoUserId);
}