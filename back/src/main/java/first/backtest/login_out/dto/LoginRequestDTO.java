package first.backtest.login_out.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequestDTO {
    @NotBlank//(message = "사용자명은 필수입니다.")
    private String username;

    @NotBlank//(message = "비밀번호는 필수입니다.")
    private String password;
}
