package com.springboot.dto.auth;

import com.springboot.entity.User;
import lombok.*;

// 🔵 기존 + 🟢 확장: 통합 인증 응답 DTO
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {
    private String token;
    private String refreshToken;
    private UserDTO user;

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserDTO {
        private Long id;
        private String name;
        private String email;
        private String username;  // 🟢 새로 추가
        private String profileImage;
        private String loginType;  // 🟢 새로 추가: KAKAO, NAVER, GENERAL
        private Boolean emailVerified;  // 🟢 새로 추가

        // 🟢 새로 추가: User 엔티티에서 UserDTO로 변환
        public static UserDTO fromEntity(User user) {
            return UserDTO.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .profileImage(user.getProfileImage())
                    .loginType(user.getLoginType() != null ? user.getLoginType().name() : null)
                    .emailVerified(user.getEmailVerified())
                    .build();
        }
    }
}