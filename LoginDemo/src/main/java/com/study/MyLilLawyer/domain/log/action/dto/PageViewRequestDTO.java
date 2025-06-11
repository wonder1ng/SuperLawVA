package com.study.MyLilLawyer.domain.log.dto;

public record PageViewRequestDTO (
    String action,     // "start" | "end"
    Long viewId,
    Long sessionId,
    String path
) {}
