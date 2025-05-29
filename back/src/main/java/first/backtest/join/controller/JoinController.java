package first.backtest.join.controller;

import first.backtest.join.UserCreateForm;
import first.backtest.join.service.JoinService;
import first.backtest.join.service.MailService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import first.backtest.join.dto.UserJoinResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/user")
public class JoinController {

    private final JoinService joinService;
    private final MailService mailService;

    @GetMapping("/find-by-id")
    public ResponseEntity<UserJoinResponseDTO> getUser(@RequestParam Long id) {
        UserJoinResponseDTO user = joinService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody UserCreateForm userCreateForm,
                                  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("입력값이 유효하지 않습니다.");
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            return ResponseEntity.badRequest().body("비밀번호가 서로 일치하지 않습니다.");
        }

        joinService.create(
                userCreateForm.getUsername(),
                userCreateForm.getEmail(),
                userCreateForm.getPassword1()
        );

        // 🟦 [추가] 인증번호 생성
        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        // 🟦 [추가] 인증메일 전송
        mailService.sendAuthMail(userCreateForm.getEmail(), code);

        // 🟦 [추가] 인증번호 저장은 추후 Redis/DB 연동 시 구현
        // TODO: 인증번호를 Redis 또는 DB에 저장하고, 유효시간 설정 추천

        return ResponseEntity.ok("회원가입 성공! 인증메일이 전송되었습니다.");
    }
}








