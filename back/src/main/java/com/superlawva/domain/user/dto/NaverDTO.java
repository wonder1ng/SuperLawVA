package com.superlawva.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

public class NaverDTO {

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TokenResponse {
        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("refresh_token")
        private String refreshToken;

        @JsonProperty("token_type")
        private String tokenType;

        @JsonProperty("expires_in")
        private Integer expiresIn;
    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NaverProfile {
        @JsonProperty("response")
        private NaverAccount naverAccount;

        @Getter @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class NaverAccount {
            private String id;
            private String email;
            private String name;
            private String nickname;
            private String profileImage;
        }
    }
} 