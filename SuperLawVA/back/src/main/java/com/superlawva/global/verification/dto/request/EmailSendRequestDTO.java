package com.superlawva.global.verification.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;

@Getter
public class EmailSendRequestDTO {
    @Email
    private String email;
}
