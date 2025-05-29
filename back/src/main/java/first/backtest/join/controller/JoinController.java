package first.backtest.join.controller;

import first.backtest.join.UserCreateForm;
import first.backtest.join.dto.UserJoinResponseDTO;
import first.backtest.join.dto.VerifyRequestDTO;
import first.backtest.join.service.JoinService;
import first.backtest.join.service.MailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class JoinController {

    private final JoinService joinService;
    private final MailService mailService;

    /**
     * ID로 회원 정보 조회
     */
    @GetMapping("/find-by-id")
    public ResponseEntity<UserJoinResponseDTO> getUser(@RequestParam Long id) {
        return ResponseEntity.ok(joinService.getUserById(id));
    }

    /**
     * 회원가입 + 인증메일 전송
     */
    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody UserCreateForm userCreateForm,
                                  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("입력값이 유효하지 않습니다.");
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            return ResponseEntity.badRequest().body("비밀번호가 서로 일치하지 않습니다.");
        }

        // 사용자 저장
        joinService.create(
                userCreateForm.getUsername(),
                userCreateForm.getEmail(),
                userCreateForm.getPassword1()
        );

        // 인증번호 생성
        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        // 인증메일 전송 + Redis 저장
        mailService.sendAuthMail(userCreateForm.getEmail(), code);

        return ResponseEntity.ok("회원가입 성공! 인증메일이 전송되었습니다.");
    }

    /**
     * 이메일 인증번호 검증
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(@RequestBody VerifyRequestDTO request) {
        try {
            mailService.verifyCode(request.getEmail(), request.getCode());
            return ResponseEntity.ok("이메일 인증 성공!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}







