package com.superlawva.global.verification.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class EmailVerifyRequestDTO {

    @Email
    private String email;

    @Size(min = 6, max = 6)
    private String code;
}
