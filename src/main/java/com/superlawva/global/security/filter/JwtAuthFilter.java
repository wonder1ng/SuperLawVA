package com.superlawva.global.security.filter;

import com.superlawva.domain.user.entity.User;
import com.superlawva.domain.user.repository.UserRepository;
import com.superlawva.global.response.status.ErrorStatus; // [MOD] import ErrorStatus
import com.superlawva.global.security.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
    private final UserRepository userRepository;

    private static final List<String> EXCLUDE_URLS = List.of(
            "/swagger-ui", "/swagger-resources", "/v3/api-docs",
            "/oauth2", "/login", "/"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return EXCLUDE_URLS.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        String token = jwtUtil.getJwtFromRequest(req);

        if (token == null) {
            chain.doFilter(req, res);
            return;
        }

        if (!jwtUtil.validate(token)) {
            // [MOD] ErrorStatus enum 사용
            ErrorStatus status = ErrorStatus.INVALID_OR_EXPIRED_TOKEN;
            sendError(res, HttpServletResponse.SC_UNAUTHORIZED,
                    status.getCode(), status.getMessage());
            return;
        }

        Long kakaoId = jwtUtil.extractKakaoId(token);
        userRepository.findByKakaoId(kakaoId).ifPresent(user -> {
            var auth = new UsernamePasswordAuthenticationToken(
                    user.getEmail(), null, List.of()
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        });

        chain.doFilter(req, res);
    }

    private void sendError(HttpServletResponse res,
                           int statusCode,
                           String errorCode,
                           String message) throws IOException {
        res.setStatus(statusCode);
        res.setContentType("application/json; charset=UTF-8");
        res.getWriter().write(
                String.format("{\"error\":\"%s\",\"message\":\"%s\"}", errorCode, message)
        );
    }
}