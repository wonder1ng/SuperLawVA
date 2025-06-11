package com.superlawva.domain.user.dto;

import com.superlawva.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserResponseDTO {
    private Long   id;
    private Long   kakaoId;
    private String email;
    private String nickname;
    private String role;

    public static UserResponseDTO from(User m) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.id       = m.getId();
        dto.kakaoId  = m.getKakaoId();
        dto.email    = m.getEmail();
        dto.nickname = m.getNickname();
        dto.role     = m.getRole().name();
        return dto;
    }
}

