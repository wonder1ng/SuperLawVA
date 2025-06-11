package com.study.logindemo.global.security.controller;

import com.study.logindemo.global.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TokenController {

    private final JwtUtil jwtUtil;

    @GetMapping("/token/{kakaoId}")
    public String token(@PathVariable Long kakaoId) {
        return jwtUtil.generateToken(kakaoId);   // 과거 createToken → generateToken
    }
}
