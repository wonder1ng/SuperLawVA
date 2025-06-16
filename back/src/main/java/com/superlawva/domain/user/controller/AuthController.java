package com.superlawva.domain.user.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.superlawva.domain.user.dto.AuthResponseDTO;
import com.superlawva.domain.user.dto.EmailVerificationDTO;
import com.superlawva.domain.user.dto.LoginRequestDTO;
import com.superlawva.domain.user.dto.RegisterRequestDTO;
import com.superlawva.domain.user.dto.RegisterResponseDTO;

import com.superlawva.domain.user.entity.User;
import com.superlawva.global.security.CustomUserDetails;
import com.superlawva.domain.user.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "통합 인증 API", description = "소셜 로그인, 일반 로그인 및 JWT 토큰 관리 API")
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 🔵 기존: 소셜 로그인 엔드포인트들
    @Operation(
            summary = "카카오 로그인",
            description = "카카오 OAuth 인가 코드를 통해 로그인하고 JWT 토큰을 발급받습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 인가 코드"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/kakao/callback")
    public ResponseEntity<AuthResponseDTO> kakaoCallback(
            @Parameter(description = "카카오 OAuth 인가 코드", required = true)
            @RequestBody Map<String, String> request) {
        String code = request.get("code");
        log.info("Received Kakao authorization code: {}", code);
        AuthResponseDTO response = authService.kakaoLogin(code);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "네이버 로그인",
            description = "네이버 OAuth 인가 코드를 통해 로그인하고 JWT 토큰을 발급받습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 인가 코드"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/naver/callback")
    public ResponseEntity<AuthResponseDTO> naverCallback(
            @Parameter(description = "네이버 OAuth 인가 코드", required = true)
            @RequestBody Map<String, String> request) {
        String code = request.get("code");
        log.info("Received Naver authorization code: {}", code);
        AuthResponseDTO response = authService.naverLogin(code);
        return ResponseEntity.ok(response);
    }

    // 🟢 수정: Redis 기반 이메일 인증을 사용한 회원가입
    @Operation(
            summary = "일반 회원가입",
            description = "사용자명, 이메일, 비밀번호로 회원가입하고 Redis를 통한 인증 이메일을 발송합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류 또는 중복 사용자"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Parameter(description = "회원가입 정보", required = true)
            @Valid @RequestBody RegisterRequestDTO registerRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(Map.of("error", "입력값이 유효하지 않습니다."));
        }

        try {
            // 🟢 Redis 기반 이메일 인증 회원가입
            RegisterResponseDTO response = authService.registerWithRedisVerification(registerRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 🟢 새로 추가: 일반 로그인
    @Operation(
            summary = "일반 로그인",
            description = "사용자명(또는 이메일)과 비밀번호로 로그인하고 JWT 토큰을 발급받습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Parameter(description = "로그인 정보", required = true)
            @Valid @RequestBody LoginRequestDTO loginRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(Map.of("message", "입력값이 유효하지 않습니다."));
        }

        try {
            AuthResponseDTO response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // 🟢 수정: Redis 기반 이메일 인증 (React 연동)
    @Operation(
            summary = "이메일 인증",
            description = "Redis에 저장된 인증 코드로 이메일 인증을 완료하고 선택적으로 토큰을 발급합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 성공"),
            @ApiResponse(responseCode = "403", description = "잘못된 인증 코드 또는 만료"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(
            @Parameter(description = "이메일 인증 정보", required = true)
            @Valid @RequestBody EmailVerificationDTO verificationRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(Map.of("message", "입력값이 유효하지 않습니다."));
        }

        try {
            // 🟢 Redis 기반 이메일 인증 처리
            authService.verifyEmailWithRedis(verificationRequest);

            // 🟢 React가 기대하는 응답: 인증 완료 후 토큰 발급 (선택적)
            // React에서 즉시 로그인을 원한다면 토큰 발급
            User user = authService.getCurrentUserByEmail(verificationRequest.getEmail());
            AuthResponseDTO authResponse = authService.generateAuthResponseForUser(user);

            return ResponseEntity.ok(Map.of(
                    "message", "이메일 인증 성공!",
                    "token", authResponse.getToken(),
                    "refreshToken", authResponse.getRefreshToken(),
                    "user", authResponse.getUser()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("message", e.getMessage()));
        }
    }

    // 🟢 새로 추가: 인증 코드 재발송 (Redis 기반)
    @Operation(
            summary = "인증 코드 재발송",
            description = "Redis를 통해 이메일 인증 코드를 다시 발송합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재발송 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 이메일"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "이메일은 필수입니다."));
        }

        try {
            boolean result = authService.resendRedisVerificationCode(email);
            return ResponseEntity.ok(Map.of(
                    "success", result,
                    "message", result ? "인증 코드가 재발송되었습니다." : "인증 코드 재발송에 실패했습니다."
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 🔵 기존: 토큰 갱신
    @Operation(
            summary = "토큰 갱신",
            description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 갱신 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 리프레시 토큰"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(
            @Parameter(description = "리프레시 토큰", required = true)
            @RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        AuthResponseDTO response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    // 🔵 기존: 토큰 유효성 검증
    @Operation(
            summary = "토큰 유효성 검증",
            description = "JWT 토큰의 유효성을 검증합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검증 완료"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Boolean>> validateToken(
            @Parameter(description = "Bearer JWT 토큰", required = true)
            @RequestHeader("Authorization") String bearerToken) {
        String token = bearerToken.substring(7);
        try {
            authService.validateToken(token);
            return ResponseEntity.ok(Map.of("valid", true));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("valid", false));
        }
    }

    // 🔵 기존: 현재 사용자 정보 조회
    @Operation(
            summary = "현재 사용자 정보 조회",
            description = "JWT 토큰을 통해 현재 로그인한 사용자의 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/me")
    public ResponseEntity<AuthResponseDTO.UserDTO> getCurrentUser(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        AuthResponseDTO.UserDTO userDTO = authService.getCurrentUser(userDetails.getId());
        return ResponseEntity.ok(userDTO);
    }

    // 🟢 새로 추가: 로그아웃
    @Operation(
            summary = "로그아웃",
            description = "현재 사용자를 로그아웃합니다. (클라이언트에서 토큰 삭제 필요)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        // JWT는 상태를 저장하지 않으므로 클라이언트에서 토큰을 삭제하는 것으로 로그아웃 처리
        return ResponseEntity.ok(Map.of("message", "로그아웃 성공"));
    }

    // 🟢 새로 추가: 회원 탈퇴
    @Operation(
            summary = "회원 탈퇴",
            description = "현재 로그인한 사용자의 계정을 완전히 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "탈퇴 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/withdraw")
    public ResponseEntity<Map<String, String>> deleteUser(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            authService.deleteUser(userDetails.getId());
            return ResponseEntity.ok(Map.of("message", "회원 탈퇴가 완료되었습니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 🟢 새로 추가: ID로 사용자 조회 (관리자 기능)
    @Operation(
            summary = "ID로 사용자 조회",
            description = "사용자 ID로 특정 사용자 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/user/{id}")
    public ResponseEntity<AuthResponseDTO.UserDTO> getUserById(@PathVariable Long id) {
        try {
            AuthResponseDTO.UserDTO userDTO = authService.getCurrentUser(id);
            return ResponseEntity.ok(userDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}