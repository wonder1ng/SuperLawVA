package first.backtest.join.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import first.backtest.join.dto.UserJoinResponseDTO;
import first.backtest.user.UserEntity;
import first.backtest.user.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class JoinService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Bean으로 주입받아 사용

    @Transactional
    public UserEntity create(String username, String email, String password) {

        // 중복 사용자명 체크
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("이미 존재하는 사용자명입니다.");
        }

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // Bean으로 주입받은 인코더 사용
        this.userRepository.save(user);
        return user;
    }

    public UserJoinResponseDTO getUserById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 ID의 사용자를 찾을 수 없습니다."));
        return UserJoinResponseDTO.fromEntity(user);
    }
}
