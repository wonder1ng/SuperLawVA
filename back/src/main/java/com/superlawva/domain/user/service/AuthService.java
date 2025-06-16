package com.superlawva.domain.user.service;

import com.superlawva.domain.user.dto.*;
import com.superlawva.domain.user.entity.User;
import com.superlawva.domain.user.repository.UserRepository;
import com.superlawva.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    // 🔵 기존 소셜 로그인 의존성들
    private final KakaoService kakaoService;
    private final NaverService naverService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 🟢 새로 추가: 일반 로그인 의존성들
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    // 🟢 Redis 기반 이메일 서비스
    private final RedisEmailService redisEmailService;
    private final StringRedisTemplate redisTemplate;

    // 🔵 기존: 카카오 로그인
    @Transactional
    public AuthResponseDTO kakaoLogin(String code) {
        KakaoDTO.TokenResponse tokenResponse = kakaoService.getKakaoTokenInfo(code);
        KakaoDTO.KakaoProfile profile = kakaoService.getKakaoProfile(tokenResponse.getAccessToken());
        User user = getUserFromKakaoProfile(profile);
        return generateAuthResponse(user);
    }

    // 🔵 기존: 네이버 로그인
    @Transactional
    public AuthResponseDTO naverLogin(String code) {
        NaverDTO.TokenResponse tokenResponse = naverService.getNaverTokenInfo(code);
        NaverDTO.NaverProfile profile = naverService.getNaverProfile(tokenResponse.getAccessToken());
        User user = getUserFromNaverProfile(profile);
        return generateAuthResponse(user);
    }

    // 🟢 수정: Redis 기반 회원가입
    @Transactional
    public RegisterResponseDTO registerWithRedisVerification(RegisterRequestDTO registerRequest) {
        // 1. 비밀번호 일치 확인
        if (!registerRequest.isPasswordMatch()) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 2. 중복 사용자 확인
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("이미 존재하는 사용자명입니다.");
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        // 3. Redis 인증 코드 생성 (6자리)
        String verificationCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        // 4. 사용자 생성 (이메일 인증 전이므로 emailVerified = false)
        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .loginType(User.LoginType.GENERAL)
                .emailVerified(false)  // Redis 인증 완료 후 true로 변경
                .roles(new HashSet<>(Collections.singleton("ROLE_USER")))
                .build();

        user = userRepository.save(user);

        // 5. Redis에 인증 코드 저장 (5분 TTL) + 이메일 발송
        boolean emailSent = redisEmailService.sendAuthEmail(user.getEmail(), verificationCode);

        // 6. 응답 생성
        return RegisterResponseDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .emailSent(emailSent)
                .emailMessage(emailSent ? "인증 이메일이 발송되었습니다." : "이메일 발송에 실패했습니다.")
                .devMode(false)  // 실제 이메일 발송
                .verificationRequired(true)
                .build();
    }

    // 🟢 수정: Redis 기반 이메일 인증 (React 연동)
    @Transactional
    public void verifyEmailWithRedis(EmailVerificationDTO verificationRequest) {
        // 1. Redis에서 인증 코드 검증
        redisEmailService.verifyCode(verificationRequest.getEmail(), verificationRequest.getVerificationCode());

        // 2. 사용자 조회 및 이메일 인증 완료 처리
        User user = userRepository.findByEmail(verificationRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        user.setEmailVerified(true);
        userRepository.save(user);

        log.info("이메일 인증 완료: {}", verificationRequest.getEmail());
    }

    // 🟢 새로 추가: Redis 기반 인증 코드 재발송
    @Transactional
    public boolean resendRedisVerificationCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (user.getEmailVerified()) {
            throw new RuntimeException("이미 인증된 사용자입니다.");
        }

        // 새 인증 코드 생성 및 Redis 저장
        String newVerificationCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return redisEmailService.sendAuthEmail(email, newVerificationCode);
    }

    // 🟢 새로 추가: 일반 로그인
    @Transactional
    public AuthResponseDTO login(LoginRequestDTO loginRequest) {
        try {
            // 1. 사용자 조회 (이메일 또는 사용자명)
            User user = userRepository.findByEmailOrUsername(loginRequest.getUsernameOrEmail())
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            // 2. 일반 로그인 사용자인지 확인
            if (!user.isGeneralUser()) {
                throw new RuntimeException("소셜 로그인 사용자입니다. 해당 소셜 서비스로 로그인해주세요.");
            }

            // 3. 이메일 인증 확인
            if (!user.getEmailVerified()) {
                throw new RuntimeException("이메일 인증이 완료되지 않았습니다. 이메일을 확인해주세요.");
            }

            // 4. 비밀번호 검증
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // 5. JWT 토큰 생성
            return generateAuthResponse(user);

        } catch (BadCredentialsException e) {
            throw new RuntimeException("아이디 또는 비밀번호가 잘못되었습니다.");
        }
    }

    // 🟢 새로 추가: 회원 탈퇴
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // Redis에서 해당 사용자의 인증 코드 삭제 (있다면)
        if (user.getEmail() != null) {
            redisTemplate.delete("auth:" + user.getEmail());
        }

        // 사용자 완전 삭제
        userRepository.deleteById(userId);
        log.info("사용자 탈퇴 완료: ID={}, Email={}", userId, user.getEmail());
    }

    // 🔵 기존: 토큰 갱신
    @Transactional
    public AuthResponseDTO refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("유효하지 않은 리프레시 토큰입니다.");
        }

        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (!user.getRefreshToken().equals(refreshToken)) {
            throw new RuntimeException("리프레시 토큰이 일치하지 않습니다.");
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(user);
        return AuthResponseDTO.builder()
                .token(newAccessToken)
                .refreshToken(refreshToken)
                .user(AuthResponseDTO.UserDTO.fromEntity(user))
                .build();
    }

    // 🔵 기존: 토큰 검증
    public AuthResponseDTO validateToken(String token) {
        boolean isValid = jwtTokenProvider.validateToken(token);
        if (!isValid) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        Long userId = jwtTokenProvider.getIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return AuthResponseDTO.builder()
                .token(token)
                .user(AuthResponseDTO.UserDTO.fromEntity(user))
                .build();
    }

    // 🔵 기존: 현재 사용자 정보 조회
    public AuthResponseDTO.UserDTO getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return AuthResponseDTO.UserDTO.fromEntity(user);
    }

    // 🔵 기존: 카카오 프로필에서 사용자 생성/조회
    private User getUserFromKakaoProfile(KakaoDTO.KakaoProfile profile) {
        Optional<User> existingUser = userRepository.findByKakaoId(profile.getId());
        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        User newUser = User.builder()
                .kakaoId(profile.getId())
                .email(profile.getKakaoAccount().getEmail())
                .name(profile.getKakaoAccount().getProfile().getNickname())
                .profileImage(profile.getKakaoAccount().getProfile().getProfileImageUrl())
                .loginType(User.LoginType.KAKAO)
                .emailVerified(true)  // 소셜 로그인은 이메일 인증된 것으로 간주
                .roles(new HashSet<>(Collections.singleton("ROLE_USER")))
                .build();

        return userRepository.save(newUser);
    }

    // 🔵 기존: 네이버 프로필에서 사용자 생성/조회
    private User getUserFromNaverProfile(NaverDTO.NaverProfile profile) {
        Optional<User> existingUser = userRepository.findByNaverId(profile.getNaverAccount().getId());
        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        User newUser = User.builder()
                .naverId(profile.getNaverAccount().getId())
                .email(profile.getNaverAccount().getEmail())
                .name(profile.getNaverAccount().getName())
                .profileImage(profile.getNaverAccount().getProfileImage())
                .loginType(User.LoginType.NAVER)
                .emailVerified(true)  // 소셜 로그인은 이메일 인증된 것으로 간주
                .roles(new HashSet<>(Collections.singleton("ROLE_USER")))
                .build();

        return userRepository.save(newUser);
    }

    // 🟢 공통: JWT 토큰 응답 생성
    private AuthResponseDTO generateAuthResponse(User user) {
        String accessToken = jwtTokenProvider.createAccessToken(user);
        String refreshToken = jwtTokenProvider.createRefreshToken(user);

        // 리프레시 토큰 저장
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return AuthResponseDTO.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .user(AuthResponseDTO.UserDTO.fromEntity(user))
                .build();
    }

    // 🟢 기존 이메일 서비스와의 호환성 유지 (레거시)
    @Deprecated
    public RegisterResponseDTO register(RegisterRequestDTO registerRequest) {
        return registerWithRedisVerification(registerRequest);
    }

    @Deprecated
    public AuthResponseDTO verifyEmail(EmailVerificationDTO verificationRequest) {
        verifyEmailWithRedis(verificationRequest);

        // 인증 완료 후 토큰 발급
        User user = userRepository.findByEmail(verificationRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return generateAuthResponse(user);
    }

    @Deprecated
    public boolean resendVerificationCode(String email) {
        return resendRedisVerificationCode(email);
    }

    // 🟢 새로 추가: 이메일로 사용자 조회 (내부 메서드)
    public User getCurrentUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

    // 🟢 새로 추가: 사용자 객체로 인증 응답 생성 (내부 메서드)
    public AuthResponseDTO generateAuthResponseForUser(User user) {
        return generateAuthResponse(user);
    }
}