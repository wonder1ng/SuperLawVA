package com.study.logindemo.global.security.filter;

import com.study.logindemo.domain.member.repository.MemberRepository;
import com.study.logindemo.global.security.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        /* 1) 쿠키에서 토큰 추출 */
        String token = null;
        if (req.getCookies() != null) {
            for (Cookie c : req.getCookies()) {
                if ("ACCESS_TOKEN".equals(c.getName())) {
                    token = c.getValue();
                    break;
                }
            }
        }

        /* 2) 토큰 검증 → SecurityContext 등록 */
        if (token != null && jwtUtil.validate(token)) {
            Long kakaoId = jwtUtil.extractKakaoId(token);

            memberRepo.findByKakaoId(kakaoId).ifPresent(m -> {
                var auth = new UsernamePasswordAuthenticationToken(
                        kakaoId, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(auth);
            });
        }

        chain.doFilter(req, res);
    }
}
