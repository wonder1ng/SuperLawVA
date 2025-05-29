package first.backtest.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // application.yml에서 설정한 JWT 비밀키와 만료시간을 주입받음
    @Value("${jwt.secret}")
    private String secret;

    //@Value("${jwt.expiration}")
    //private Long expiration;

    // JWT 서명에 사용할 키를 생성하는 메서드
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * JWT 토큰을 생성하는 메서드
     * @param username 사용자명 (토큰의 주체가 됨)
     * @return 생성된 JWT 토큰 문자열
     */
    public String generateToken(String username) {
        Date now = new Date();
        //Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(username)        // 토큰의 주체 (사용자명)
                .setIssuedAt(now)           // 토큰 발급 시간
                //.setExpiration(expiryDate)   // 토큰 만료 시간
                .signWith(getSigningKey())   // 서명 (토큰이 위조되지 않았음을 증명)
                .compact();                  // 최종 토큰 문자열 생성
    }

    /**
     * JWT 토큰에서 사용자명을 추출하는 메서드
     * @param token JWT 토큰
     * @return 토큰에 담긴 사용자명
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * JWT 토큰이 유효한지 검증하는 메서드
     * @param token 검증할 JWT 토큰
     * @return 토큰이 유효하면 true, 아니면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 토큰이 유효하지 않거나 만료된 경우
            return false;
        }
    }
}