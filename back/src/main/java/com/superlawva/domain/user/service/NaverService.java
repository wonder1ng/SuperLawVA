package com.superlawva.domain.user.service;

import com.superlawva.domain.user.dto.NaverDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverService {

    private final WebClient webClient;

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    @Value("${naver.redirect-uri}")
    private String redirectUri;

    // 네이버 인가 코드로 액세스 토큰 요청
    public NaverDTO.TokenResponse getNaverTokenInfo(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("code", code);
        formData.add("state", "RANDOM_STATE"); // CSRF 방지를 위한 state 값

        log.info("네이버 토큰 요청 파라미터: {}", formData);

        return webClient.post()
                .uri("https://nid.naver.com/oauth2.0/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(NaverDTO.TokenResponse.class)
                .block();
    }

    // 네이버 액세스 토큰으로 사용자 정보 요청
    public NaverDTO.NaverProfile getNaverProfile(String accessToken) {
        return webClient.get()
                .uri("https://openapi.naver.com/v1/nid/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(NaverDTO.NaverProfile.class)
                .block();
    }
} 