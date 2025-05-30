package com.springboot.service;

import com.springboot.dto.*;
import com.springboot.entity.User;
import com.springboot.repository.UserRepository;
import com.springboot.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final EmailService emailService;  // 이메일 서비스

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

    // 🟢 새로 추가: 일반 회원가입 (React 연동)
    @Transactional
    public RegisterResponseDTO register(RegisterRequestDTO registerRequest) {
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

        // 3. 이메일 인증 코드 생성
        String verificationCode = generateVerificationCode();
        LocalDateTime codeExpiry = LocalDateTime.now().plusHours(24); // 24시간 유효

        // 4. 사용자 생성
        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .loginType(User.LoginType.GENERAL)
                .emailVerified(false)
                .verificationCode(verificationCode)
                .verificationCodeExpiry(codeExpiry)
                .roles(new HashSet<>(Collections.singleton("ROLE_USER")))
                .build();

        user = userRepository.save(user);

        // 5. 이메일 발송 (React 연동 방식)
        EmailService.EmailVerificationResult emailResult =
                emailService.sendVerificationEmail(user.getEmail(), verificationCode);

        // 6. React용 응답 생성 (토큰은 이메일 인증 후에 발급)
        return RegisterResponseDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .emailSent(emailResult.isSuccess())
                .emailMessage(emailResult.getMessage())
                .devMode(emailResult.isDevMode())
                .verificationRequired(true)
                // 개발 모드일 때만 인증 코드 포함
                .verificationCode(emailResult.isDevMode() ? verificationCode : null)
                .build();
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

            // 3. 비밀번호 검증
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // 4. JWT 토큰 생성
            return generateAuthResponse(user);

        } catch (BadCredentialsException e) {
            throw new RuntimeException("아이디 또는 비밀번호가 잘못되었습니다.");
        }
    }

    // 🟢 새로 추가: 이메일 인증
    @Transactional
    public AuthResponseDTO verifyEmail(EmailVerificationDTO verificationRequest) {
        User user = userRepository.findByEmailAndVerificationCode(
                verificationRequest.getEmail(),
                verificationRequest.getVerificationCode()
        ).orElseThrow(() -> new RuntimeException("잘못된 인증 코드입니다."));

        // 인증 코드 만료 확인
        if (user.getVerificationCodeExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("인증 코드가 만료되었습니다.");
        }

        // 이메일 인증 완료
        user.setEmailVerified(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiry(null);
        userRepository.save(user);

        return generateAuthResponse(user);
    }

    // 🟢 새로 추가: 인증 코드 재발송
    @Transactional
    public boolean resendVerificationCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (user.getEmailVerified()) {
            throw new RuntimeException("이미 인증된 사용자입니다.");
        }

        // 새 인증 코드 생성
        String newVerificationCode = generateVerificationCode();
        LocalDateTime newExpiry = LocalDateTime.now().plusHours(24);

        user.setVerificationCode(newVerificationCode);
        user.setVerificationCodeExpiry(newExpiry);
        userRepository.save(user);

        // 이메일 재발송
        EmailService.EmailVerificationResult result =
                emailService.sendVerificationEmail(email, newVerificationCode);

        return result.isSuccess();
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

    // 🟢 새로 추가: 인증 코드 생성
    private String generateVerificationCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}