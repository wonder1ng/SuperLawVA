package com.study.MyLilLawyer.domain.log.dto;

import com.study.MyLilLawyer.domain.log.entity.Device;

public record SessionRequestDTO(
        String action,     // "start" | "end"
        Long sessionId,
        Long userId,
        Device device
) {}