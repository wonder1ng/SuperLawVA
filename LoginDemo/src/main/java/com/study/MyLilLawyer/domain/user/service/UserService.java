package com.study.MyLilLawyer.domain.member.service;

import com.study.MyLilLawyer.domain.member.dto.MemberRequestDTO;
import com.study.MyLilLawyer.domain.member.dto.MemberResponseDTO;

import java.util.List;

public interface MemberService {
    List<MemberResponseDTO> findAll();
    MemberResponseDTO findById(Long id);
    MemberResponseDTO create(MemberRequestDTO dto);
    MemberResponseDTO update(Long id, MemberRequestDTO dto);
    void delete(Long id);
}