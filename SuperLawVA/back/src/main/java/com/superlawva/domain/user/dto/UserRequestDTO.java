package com.superlawva.domain.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;

@Getter
public class UserRequestDTO {

    @Email
    private String email;
    private Long kakaoId;
    private String nickname;
}
