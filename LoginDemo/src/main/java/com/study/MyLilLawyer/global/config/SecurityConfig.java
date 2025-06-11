package com.study.logindemo.global.config;

import com.study.logindemo.domain.member.repository.MemberRepository;
import com.study.logindemo.global.security.filter.JwtAuthFilter;
import com.study.logindemo.global.security.handler.OAuth2LoginSuccessHandler;
import com.study.logindemo.global.security.service.CustomOAuth2UserService;
import com.study.logindemo.global.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService oAuth2UserService;
    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepo;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> { })
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/members/**").authenticated()
                        .anyRequest().permitAll())
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(u -> u.userService(oAuth2UserService))
                        .successHandler(new OAuth2LoginSuccessHandler(jwtUtil)));

        http.addFilterBefore(
                new JwtAuthFilter(jwtUtil, memberRepo),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
