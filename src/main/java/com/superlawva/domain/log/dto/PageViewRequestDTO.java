package com.superlawva.domain.log.dto;

public record PageViewRequestDTO (
    String action,     // "start" | "end"
    Long viewId,
    Long sessionId,
    String path
) {}
