package com.superlawva.global.security.service;

import com.superlawva.domain.user.entity.User;
import com.superlawva.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService
        implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepo;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest req)
            throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate =
                new DefaultOAuth2UserService();
        OAuth2User raw = delegate.loadUser(req);

        if (!"kakao".equals(req.getClientRegistration().getRegistrationId())) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("invalid_provider", "Only kakao", null));
        }

        Map<String, Object> attr = raw.getAttributes();
        Long kakaoId = ((Number) attr.get("id")).longValue();

        Map<String, Object> account = (Map<String, Object>) attr.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");

        String nickname = (String) profile.get("nickname");

        /* 이메일: 없으면 가짜 */
        String rawEmail = (String) account.get("email");
        final String email = (rawEmail == null || rawEmail.isBlank())
                ? "kakao_" + kakaoId + "@kakao.local"
                : rawEmail;

        User user = userRepo.findByKakaoId(kakaoId)
                .map(u -> { u.changeNickname(nickname); return u; })
                .orElseGet(() -> userRepo.save(User.builder()
                        .kakaoId(kakaoId)
                        .email(email)
                        .nickname(nickname)
                        .build()));

        return new DefaultOAuth2User(
                Set.of(new SimpleGrantedAuthority(user.getRole().name())),
                attr,
                "id");
    }
}
