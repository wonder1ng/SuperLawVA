package com.superlawva.domain.log.dto;

import com.superlawva.domain.log.entity.Device;

public record SessionRequestDTO(
        String action,     // "start" | "end"
        Long sessionId,
        Long userId,
        Device device
) {}