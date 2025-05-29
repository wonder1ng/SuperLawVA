package first.backtest.join.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//이메일 인증번호를 검증하는 DTO (email, code)
public class VerifyRequestDTO {
    private String email;
    private String code;
}