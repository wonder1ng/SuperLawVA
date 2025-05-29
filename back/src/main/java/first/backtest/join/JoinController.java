package first.backtest.join;

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

@RequiredArgsConstructor
@RestController
@RequestMapping("api/user")
public class JoinController {

    private final JoinService joinService;

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

        joinService.create(userCreateForm.getUsername(), userCreateForm.getEmail(), userCreateForm.getPassword1());

        return ResponseEntity.ok("회원가입 성공");
    }
}
