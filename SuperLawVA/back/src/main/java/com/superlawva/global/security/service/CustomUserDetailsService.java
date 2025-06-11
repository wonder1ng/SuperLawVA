package com.superlawva.global.security.service;

import com.superlawva.domain.user.entity.User;
import com.superlawva.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ▒ JWT / 세션이 가진 userId(kakaoId)를 통해 UserDetails 를 빌드 ▒
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String kakaoId)
            throws UsernameNotFoundException {

        User m = userRepo.findByKakaoId(Long.valueOf(kakaoId))
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found : " + kakaoId));

        return new org.springframework.security.core.userdetails.User(
                kakaoId,
                "",  // password 없음(소셜 전용)
                List.of(new SimpleGrantedAuthority(m.getRole().name())));
    }
}
