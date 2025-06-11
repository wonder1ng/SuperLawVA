package com.study.MyLilLawyer.domain.member.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;

@Getter
public class MemberRequestDTO {

    @Email
    private String email;
    private String nickname;
}
