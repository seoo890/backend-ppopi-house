package com.ppopi.ppopihouse.auth.service;

import com.ppopi.ppopihouse.auth.config.JwtProperties;
import com.ppopi.ppopihouse.global.exception.UnauthorizedException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(Long memberId) {
        return createToken(memberId, jwtProperties.getAccessExpiration());
    }

    public String createRefreshToken(Long memberId) {
        return createToken(memberId, jwtProperties.getRefreshExpiration());
    }

    private String createToken(Long memberId, long expiration) {
        Date now = new Date();

        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .signWith(getKey())
                .compact();
    }

    public Long getMemberId(String token) {
        try {
            return Long.valueOf(
                    Jwts.parser()
                            .verifyWith(getKey())
                            .build()
                            .parseSignedClaims(token)
                            .getPayload()
                            .getSubject()
            );
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException("유효하지 않은 토큰입니다.");
        }
    }

    public void validateOrThrow(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token);
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException("유효하지 않거나 만료된 토큰입니다.");
        }
    }

    public boolean validate(String token) {
        try {
            validateOrThrow(token);
            return true;
        } catch (UnauthorizedException e) {
            return false;
        }
    }
}