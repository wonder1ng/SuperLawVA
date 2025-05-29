package first.backtest.join.dto;

import first.backtest.user.UserEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserJoinRequestDTO {
    @NotBlank(message = "아이디는 필수입니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email
    private String email;

    public UserEntity toEntity(String encodedPassword) {
        return UserEntity.builder()
                .username(this.username)
                .email(this.email)
                .password(encodedPassword)
                .build();
    }
}
