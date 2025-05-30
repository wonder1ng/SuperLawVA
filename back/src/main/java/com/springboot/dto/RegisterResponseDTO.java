package com.springboot.dto;

import lombok.*;

// 🟢 새로 추가: React 연동용 회원가입 응답 DTO
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponseDTO {
    private Long userId;
    private String username;
    private String email;
    private boolean emailSent;           // 이메일 발송 성공 여부
    private String emailMessage;         // 이메일 발송 결과 메시지
    private boolean devMode;             // 개발 모드 여부
    private boolean verificationRequired; // 이메일 인증 필요 여부
    private String verificationCode;     // 개발 모드일 때만 포함
}