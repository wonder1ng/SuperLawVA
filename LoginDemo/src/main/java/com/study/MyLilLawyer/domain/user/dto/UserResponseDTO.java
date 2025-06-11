package com.study.MyLilLawyer.domain.member.dto;

import com.study.MyLilLawyer.domain.member.entity.Member;
import lombok.Getter;

@Getter
public class MemberResponseDTO {
    private Long   id;
    private Long   kakaoId;
    private String email;      // null 가능
    private String nickname;
    private String role;

    public static MemberResponseDTO from(Member m) {
        MemberResponseDTO dto = new MemberResponseDTO();
        dto.id       = m.getId();
        dto.kakaoId  = m.getKakaoId();
        dto.email    = m.getEmail();
        dto.nickname = m.getNickname();
        dto.role     = m.getRole().name();
        return dto;
    }
}

