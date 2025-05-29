package first.backtest.login_out.controller;

import first.backtest.login_out.service.LogIn_OutService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import first.backtest.login_out.dto.LoginRequestDTO;
import first.backtest.login_out.dto.LoginResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class LogIn_OutController {

    private final LogIn_OutService logInOutService;

    /**
     * 사용자 로그인 API
     * POST /api/user/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest,
                                   BindingResult bindingResult) {

        // 입력값 검증
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("입력값이 유효하지 않습니다.");
        }

        try {
            // 로그인 처리 (JWT 토큰 생성)
            LoginResponseDTO response = logInOutService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 사용자 로그아웃 API
     * POST /api/user/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        try {
            logInOutService.logout();
            return ResponseEntity.ok("로그아웃 성공");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
