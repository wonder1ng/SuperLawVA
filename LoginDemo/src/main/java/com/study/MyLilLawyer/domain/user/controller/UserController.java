package com.study.MyLilLawyer.domain.member.controller;

import com.study.MyLilLawyer.domain.member.dto.MemberRequestDTO;
import com.study.MyLilLawyer.domain.member.dto.MemberResponseDTO;
import com.study.MyLilLawyer.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "회원 CRUD API")
public class MemberController {

    private final MemberService service;

    @Operation(summary = "전체 회원 조회")
    @GetMapping
    public List<MemberResponseDTO> findAll() { return service.findAll(); }

    @Operation(summary = "회원 조회")
    @GetMapping("/{id}")
    public MemberResponseDTO findById(@PathVariable Long id) { return service.findById(id); }

    @Operation(summary = "회원 생성")
    @PostMapping
    public ResponseEntity<MemberResponseDTO> create(@RequestBody @Valid MemberRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @Operation(summary = "회원 정보 수정")
    @PutMapping("/{id}")
    public MemberResponseDTO update(@PathVariable Long id, @RequestBody @Valid MemberRequestDTO dto) {
        return service.update(id, dto);
    }

    @Operation(summary = "회원 삭제")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}