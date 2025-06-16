package com.superlawva.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 🟢 새로 추가: 일반 회원가입 요청 DTO
@Getter
@Setter
@NoArgsConstructor
public class RegisterRequestDTO {
    @NotBlank(message = "사용자명은 필수입니다.")
    @Size(min = 3, max = 25, message = "사용자명은 3-25자 사이여야 합니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수입니다.")
    private String passwordConfirm;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    // 🟢 비밀번호 일치 확인 메서드
    public boolean isPasswordMatch() {
        return password != null && password.equals(passwordConfirm);
    }
}