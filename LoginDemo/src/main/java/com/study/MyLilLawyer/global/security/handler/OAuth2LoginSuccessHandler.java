package com.study.logindemo.global.security.handler;

import com.study.logindemo.global.security.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.time.Duration;

@RequiredArgsConstructor
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

        res.sendRedirect("http://localhost:5173/members");
    }
}
