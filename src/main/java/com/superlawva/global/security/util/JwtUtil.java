package com.superlawva.global.security.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET = "jwt-secret-key-very-secure-and-long-enough";
    private static final SecretKey KEY =
            Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    private static final long EXPIRE_MS = Duration.ofHours(12).toMillis();

    // 기존 토큰 생성 로직
    public String generateToken(Long kakaoId) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(kakaoId))
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + EXPIRE_MS))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // 기존 검증 로직
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

    // 기존 kakaoId 추출 로직
    public Long extractKakaoId(String jwt) {
        String sub = Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject();
        return Long.valueOf(sub);
    }

    // [MOD] LoginArgumentResolver 등에서 직접 호출 가능하도록 추가
    /**
     * 토큰 유효성 검사 후 kakaoId(Long) 반환
     */
    public Long validateAndGetUserId(String jwt) {
        if (!validate(jwt)) {
            throw new IllegalArgumentException("JWT가 유효하지 않거나 만료되었습니다.");
        }
        return extractKakaoId(jwt);
    }

    // [MOD] 헤더 우선, 없으면 쿠키에서 토큰 추출하도록 추가된 메서드
    public String getJwtFromRequest(HttpServletRequest req) {
        // 헤더 검사
        String bearer = req.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        // 쿠키 검사
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("ACCESS_TOKEN".equals(c.getName()) && StringUtils.hasText(c.getValue())) {
                    return c.getValue();
                }
            }
        }
        return null;
    }
}
