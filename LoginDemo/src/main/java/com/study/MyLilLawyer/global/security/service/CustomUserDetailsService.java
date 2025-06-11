package com.study.logindemo.global.security.service;

import com.study.logindemo.domain.member.entity.Member;
import com.study.logindemo.domain.member.repository.MemberRepository;
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

    private final MemberRepository memberRepo;

    @Override
    public UserDetails loadUserByUsername(String kakaoId)
            throws UsernameNotFoundException {

        Member m = memberRepo.findByKakaoId(Long.valueOf(kakaoId))
                .orElseThrow(() ->
                        new UsernameNotFoundException("Member not found : " + kakaoId));

        return new User(
                kakaoId,
                "",  // password 없음(소셜 전용)
                List.of(new SimpleGrantedAuthority(m.getRole().name())));
    }
}
