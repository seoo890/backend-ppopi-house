package com.ppopi.ppopihouse.hospital.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppopi.ppopihouse.hospital.external.kakao.KakaoPlaceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class HospitalCacheService {

    private static final String KEY_PREFIX = "hospital:place:";
    private static final Duration TTL = Duration.ofMinutes(30);

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public void save(KakaoPlaceResponse.Document document) {
        try {
            String json = objectMapper.writeValueAsString(document);

            stringRedisTemplate.opsForValue().set(
                    KEY_PREFIX + document.id(),
                    json,
                    TTL
            );
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("병원 정보를 캐시에 저장하는 중 오류가 발생했습니다.", e);
        }
    }

    public KakaoPlaceResponse.Document get(String hospitalId) {
        String json = stringRedisTemplate.opsForValue().get(KEY_PREFIX + hospitalId);

        if (json == null) {
            return null;
        }

        try {
            return objectMapper.readValue(json, KakaoPlaceResponse.Document.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("병원 정보를 캐시에서 읽는 중 오류가 발생했습니다.", e);
        }
    }
}
