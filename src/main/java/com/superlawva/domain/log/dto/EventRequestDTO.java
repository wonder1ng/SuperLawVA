package com.superlawva.domain.log.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "이벤트 요청 DTO")
public record EventRequestDTO(

        @Schema(description = "이벤트 종류", example = "click") String type,
        @Schema(description = "이벤트 대상", example = "button.submit") String target,
        @Schema(description = "이벤트 시간", example = "2025-06-05T12:00:00Z") LocalDateTime time,
        @Schema(description = "세션 ID") Long sessionId,
        @Schema(description = "페이지뷰 ID") Long viewId,
        @Schema(description = "유저 ID", nullable = true) Long userId,
        @Schema(description = "이벤트 상세 메타데이터") Map<String, Object> meta
) {}
