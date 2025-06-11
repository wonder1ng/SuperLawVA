package com.superlawva.global.security.handler;

import com.superlawva.global.security.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;  // [MOD] 빈 등록을 위한 Component 추가

import java.io.IOException;
import java.time.Duration;

@RequiredArgsConstructor
@Component  // [MOD] OAuth2LoginSuccessHandler를 스프링 빈으로 등록
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private static final int COOKIE_MAX = (int) Duration.ofHours(12).getSeconds();

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest req,
            HttpServletResponse res,
            Authentication auth) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) auth.getPrincipal();
        Long kakaoId = ((Number) oAuth2User.getAttribute("id")).longValue();

        String jwt = jwtUtil.generateToken(kakaoId);

        Cookie cookie = new Cookie("ACCESS_TOKEN", jwt);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_MAX);
        res.addCookie(cookie);

        res.sendRedirect("http://localhost:5173/users");
    }
}
