package com.ppopi.ppopihouse.auth.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final StringRedisTemplate redisTemplate;

    private static final String PREFIX = "RT:";

    public void save(Long memberId, String refreshToken, long expiration) {
        redisTemplate.opsForValue().set(
                PREFIX + memberId,
                refreshToken,
                Duration.ofMillis(expiration)
        );
    }

    public String find(Long memberId) {
        return redisTemplate.opsForValue().get(PREFIX + memberId);
    }

    public void delete(Long memberId) {
        redisTemplate.delete(PREFIX + memberId);
    }
}