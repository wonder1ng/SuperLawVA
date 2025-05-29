package first.backtest.login_out.dto;

import first.backtest.user.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String token;        // 세션ID 대신 JWT 토큰
    private String tokenType;    // 토큰 타입 (보통 "Bearer")

    // UserEntity에서 LoginResponseDTO로 변환하는 정적 메서드
    public static LoginResponseDTO fromEntity(UserEntity user, String token) {
        return LoginResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .token(token)
                .tokenType("Bearer")  // JWT 토큰의 표준 타입
                .build();
    }
}