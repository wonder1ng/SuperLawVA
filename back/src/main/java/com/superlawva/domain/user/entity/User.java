package com.superlawva.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔵 기존 소셜 로그인 필드들
    @Column(unique = true)
    private String email;

    private String name;

    private String profileImage;

    @Column(unique = true)
    private Long kakaoId;

    @Column(unique = true)
    private String naverId;

    // 🟢 새로 추가: 일반 로그인 필드들
    @Column(unique = true)
    private String username;  // 일반 로그인용 사용자명

    private String password;  // 암호화된 비밀번호

    // 🔵 기존 공통 필드들
    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private Set<String> roles = new HashSet<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // 리프레시 토큰 저장용
    private String refreshToken;

    // 🟢 새로 추가: 이메일 인증 관련
    private Boolean emailVerified;  // 이메일 인증 여부

    private String verificationCode;  // 이메일 인증 코드

    private LocalDateTime verificationCodeExpiry;  // 인증 코드 만료 시간

    // 🟢 새로 추가: 로그인 타입 구분
    @Enumerated(EnumType.STRING)
    private LoginType loginType;  // KAKAO, NAVER, GENERAL

    public enum LoginType {
        KAKAO, NAVER, GENERAL
    }

    // 🟢 새로 추가: 소셜 로그인 사용자인지 확인
    public boolean isSocialUser() {
        return kakaoId != null || naverId != null;
    }

    // 🟢 새로 추가: 일반 로그인 사용자인지 확인
    public boolean isGeneralUser() {
        return username != null && password != null;
    }
}