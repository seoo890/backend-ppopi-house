package com.ppopi.ppopihouse.hospital.cache;

import com.ppopi.ppopihouse.hospital.external.kakao.KakaoPlaceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class HospitalCacheService {

    private static final String KEY_PREFIX = "hospital:coord:";
    private static final Duration TTL = Duration.ofMinutes(30);

    private final StringRedisTemplate stringRedisTemplate;

    public void saveCoordinate(KakaoPlaceResponse.Document document) {
        String value = document.y() + "," + document.x();

        stringRedisTemplate.opsForValue().set(
                KEY_PREFIX + document.id(),
                value,
                TTL
        );
    }

    public double[] getCoordinate(String hospitalId) {
        String value = stringRedisTemplate.opsForValue().get(KEY_PREFIX + hospitalId);

        if (value == null || value.isBlank()) {
            return null;
        }

        String[] parts = value.split(",");

        return new double[]{
                Double.parseDouble(parts[0]), // latitude
                Double.parseDouble(parts[1])  // longitude
        };
    }
}
