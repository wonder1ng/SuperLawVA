package com.study.MyLilLawyer.domain.log.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.Map;

public record EventRequestDTO(
        String type,
        String target,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        LocalDateTime time,
        Long sessionId,
        Long viewId,
        Long userId,
        Map<String, Object> meta
) {}
