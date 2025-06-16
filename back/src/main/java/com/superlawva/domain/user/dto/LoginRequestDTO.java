package com.superlawva.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 🟢 새로 추가: 일반 로그인 요청 DTO
@Getter
@Setter
@NoArgsConstructor
public class LoginRequestDTO {
    @NotBlank(message = "사용자명 또는 이메일은 필수입니다.")
    private String usernameOrEmail;  // 사용자명 또는 이메일 모두 지원

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}
