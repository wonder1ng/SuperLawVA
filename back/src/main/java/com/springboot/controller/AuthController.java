package com.springboot.controller;

import com.springboot.dto.AuthResponseDTO;
import com.springboot.security.CustomUserDetails;
import com.springboot.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "인증 API", description = "소셜 로그인 및 JWT 토큰 관리 API")
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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
}