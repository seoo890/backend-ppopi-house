package com.ppopi.ppopihouse.hospital.cache;

import com.ppopi.ppopihouse.hospital.external.kakao.KakaoPlaceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class HospitalCacheService {

    private static final String KEY_PREFIX = "hospital:place:";
    private static final Duration TTL = Duration.ofMinutes(30);

    private final RedisTemplate<String, Object> redisTemplate;

    public void save(KakaoPlaceResponse.Document document) {
        redisTemplate.opsForValue().set(
                KEY_PREFIX + document.id(),
                document,
                TTL
        );
    }

    public KakaoPlaceResponse.Document get(String hospitalId) {
        Object value = redisTemplate.opsForValue().get(KEY_PREFIX + hospitalId);

        if (value == null) {
            return null;
        }

        return (KakaoPlaceResponse.Document) value;
    }
}
