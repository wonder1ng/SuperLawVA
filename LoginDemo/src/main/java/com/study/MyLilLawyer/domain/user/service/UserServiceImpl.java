package com.study.MyLilLawyer.domain.member.service;

import com.study.MyLilLawyer.domain.member.dto.MemberRequestDTO;
import com.study.MyLilLawyer.domain.member.dto.MemberResponseDTO;
import com.study.MyLilLawyer.domain.member.entity.Member;
import com.study.MyLilLawyer.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository repo;

    @Override
    public List<MemberResponseDTO> findAll() {
        return repo.findAll().stream()
                .map(MemberResponseDTO::from)
                .toList();
    }

    @Override
    public MemberResponseDTO findById(Long id) {
        return MemberResponseDTO.from(
                repo.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Member not found")));
    }

    /** 회원 생성 (닉네임·이메일만) */
    @Override
    @Transactional
    public MemberResponseDTO create(MemberRequestDTO dto) {
        Member saved = repo.save(Member.builder()
                .email(dto.getEmail())
                .nickname(dto.getNickname())
                .build());
        return MemberResponseDTO.from(saved);
    }

    /** 닉네임 수정 */
    @Override
    @Transactional
    public MemberResponseDTO update(Long id, MemberRequestDTO dto) {
        Member m = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        m.changeNickname(dto.getNickname());
        return MemberResponseDTO.from(m);
    }

    @Override
    @Transactional
    public void delete(Long id) { repo.deleteById(id); }
}
