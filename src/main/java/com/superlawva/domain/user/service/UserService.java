package com.superlawva.domain.user.service;

import com.superlawva.domain.user.dto.UserRequestDTO;
import com.superlawva.domain.user.dto.UserResponseDTO;

import java.util.List;

public interface UserService {
    List<UserResponseDTO> findAll();
    UserResponseDTO findById(Long id);
    UserResponseDTO create(UserRequestDTO dto);
    UserResponseDTO update(Long id, UserRequestDTO dto);
    void delete(Long id);
}
