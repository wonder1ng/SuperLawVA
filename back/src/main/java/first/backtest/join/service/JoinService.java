package first.backtest.join.service;

import first.backtest.join.dto.UserJoinResponseDTO;
import first.backtest.user.UserEntity;
import first.backtest.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class JoinService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserEntity create(String username, String email, String password) {

        // 사용자명 중복 체크
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("이미 존재하는 사용자명입니다.");
        }

        // 이메일 중복 체크
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("이미 등록된 이메일입니다.");
        }

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // 비밀번호 암호화

        return userRepository.save(user);
    }

    public UserJoinResponseDTO getUserById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 ID의 사용자를 찾을 수 없습니다."));
        return UserJoinResponseDTO.fromEntity(user);
    }

    // 🔵 회원 탈퇴 메서드 추가
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId); // 완전 삭제 방식
    }
}

