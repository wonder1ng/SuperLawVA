package com.superlawva.domain.user.controller;

import com.superlawva.domain.user.dto.UserRequestDTO;
import com.superlawva.domain.user.dto.UserResponseDTO;
import com.superlawva.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    @Operation(summary = "전체 회원 조회", description = "등록된 모든 회원 정보를 조회합니다.")
    @GetMapping
    public List<UserResponseDTO> all() {
        return service.findAll();
    }

    @Operation(summary = "회원 조회", description = "ID를 이용하여 특정 회원 정보를 조회합니다.")
    @GetMapping("/{id}")
    public UserResponseDTO one(@PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(summary = "회원 등록", description = "새로운 회원 정보를 등록합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO create(@RequestBody UserRequestDTO dto) {
        return service.create(dto);
    }

    @Operation(summary = "회원 수정", description = "ID에 해당하는 회원 정보를 수정합니다.")
    @PutMapping("/{id}")
    public UserResponseDTO update(@PathVariable Long id,
                                  @RequestBody UserRequestDTO dto) {
        return service.update(id, dto);
    }

    @Operation(summary = "회원 삭제", description = "ID에 해당하는 회원 정보를 삭제합니다.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @Column(nullable = false)
    private boolean emailVerified = false;
}
