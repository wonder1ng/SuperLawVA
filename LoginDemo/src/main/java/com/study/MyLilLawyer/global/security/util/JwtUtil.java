package com.study.logindemo.global.security.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

/** kakaoId(숫자)를 JWT subject 로 사용 */
@Component
public class JwtUtil {

    /* === 키 & 만료 === */
    private static final String SECRET = "jwt-secret-key-very-secure-and-long-enough";
    private static final SecretKey KEY =
            Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    private static final long EXPIRE_MS = Duration.ofHours(12).toMillis();

    /* === 토큰 생성 === */
    public String generateToken(Long kakaoId) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(kakaoId))
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + EXPIRE_MS))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /* === 유효성 검사 === */
    public boolean validate(String jwt) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(jwt);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /* === kakaoId 추출 === */
    public Long extractKakaoId(String jwt) {
        String sub = Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject();
        return Long.valueOf(sub);
    }

    /* === 기존 코드 호환용 래퍼 === */
    public String createToken(Long kakaoId) {              // TokenController용
        return generateToken(kakaoId);
    }
    public Long validateAndGetUserId(String jwt) {         // LoginArgumentResolver용
        if (!validate(jwt)) throw new IllegalArgumentException("JWT invalid");
        return extractKakaoId(jwt);
    }
}
