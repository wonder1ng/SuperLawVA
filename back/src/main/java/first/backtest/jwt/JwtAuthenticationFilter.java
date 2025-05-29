package first.backtest.jwt;

import first.backtest.login_out.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 모든 HTTP 요청을 가로채서 JWT 토큰을 검증하는 필터
 * OncePerRequestFilter를 상속받아 요청당 한 번만 실행되도록 보장
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. HTTP 요청 헤더에서 Authorization 헤더를 가져옴
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // 2. Authorization 헤더가 "Bearer "로 시작하는지 확인 (JWT 토큰의 표준 형식)
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // "Bearer " 부분을 제거하고 토큰만 추출
            try {
                username = jwtUtil.getUsernameFromToken(token); // 토큰에서 사용자명 추출
            } catch (Exception e) {
                // 토큰에서 사용자명 추출 실패 (잘못된 토큰)
                logger.error("JWT 토큰에서 사용자명을 추출할 수 없습니다.", e);
            }
        }

        // 3. 사용자명이 존재하고 현재 SecurityContext에 인증 정보가 없다면 인증 처리
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 4. 토큰이 유효한지 검증
            if (jwtUtil.validateToken(token)) {
                // 5. 사용자 정보를 데이터베이스에서 조회
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 6. 인증 토큰 생성 (Spring Security가 이해할 수 있는 형태)
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // 7. 요청 세부정보 설정
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 8. SecurityContext에 인증 정보 저장 (이제 인증된 사용자로 간주됨)
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 9. 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }
}