package com.superlawva.domain.user.service;

import com.superlawva.domain.user.dto.UserRequestDTO;
import com.superlawva.domain.user.dto.UserResponseDTO;
import com.superlawva.domain.user.entity.User;
import com.superlawva.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository repo;

    @Override
    public List<UserResponseDTO> findAll() {
        return repo.findAll().stream()
                .map(UserResponseDTO::from)
                .toList();
    }

    @Override
    public UserResponseDTO findById(Long id) {
        return UserResponseDTO.from(
                repo.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("User id=" + id + " not found")));
    }

    @Override @Transactional
    public UserResponseDTO create(UserRequestDTO dto) {
        User saved = repo.save(User.builder()
                .email(dto.getEmail())
                .nickname(dto.getNickname())
                .kakaoId(dto.getKakaoId())
                .build());
        return UserResponseDTO.from(saved);
    }

    @Override @Transactional
    public UserResponseDTO update(Long id, UserRequestDTO dto) {
        User u = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User id=" + id + " not found"));

        // ✅ 이메일 중복 검사 (본인 이메일은 제외)
        if (!u.getEmail().equals(dto.getEmail()) && repo.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        u.changeNickname(dto.getNickname());
        u.changeEmail(dto.getEmail());

        return UserResponseDTO.from(u);
    }

    @Override @Transactional
    public void delete(Long id) { repo.deleteById(id); }


}
