package com.superlawva.domain.log.controller;

import com.superlawva.domain.log.dto.EventRequestDTO;
import com.superlawva.domain.log.dto.PageViewRequestDTO;
import com.superlawva.domain.log.dto.SessionRequestDTO;
import com.superlawva.domain.log.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "ActionLog Controller")
@RestController
@RequestMapping("/log")
@RequiredArgsConstructor
public class ActionLogController {

    private final LogService logService;

    /* ===== 세션 로그 ===== */
    @Operation(
            summary = "세션 시작/종료",
            description = "action이 \"start\"이면 세션을 생성하고 CREATED를 반환합니다. 그 외에는 종료 처리 후 NO CONTENT를 반환합니다."
    )
    @PostMapping("/session")
    public ResponseEntity<?> session(@RequestBody SessionRequestDTO req) {
        Long sessionId = logService.handleSession(req);

        if ("start".equals(req.action())) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of("session_id", sessionId));
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    /* ===== 페이지뷰 로그 ===== */
    @Operation(
            summary = "페이지뷰 시작/종료",
            description = "action이 \"start\"이면 페이지뷰를 생성하고 CREATED를 반환합니다. 그 외에는 종료 처리 후 NO CONTENT를 반환합니다."
    )
    @PostMapping("/pageview")
    public ResponseEntity<?> pageview(@RequestBody PageViewRequestDTO req) {
        Long viewId = logService.handlePageView(req);

        if ("start".equals(req.action())) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of("view_id", viewId));
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    /* ===== 이벤트 로그 ===== */
    @Operation(
            summary = "이벤트 로그 생성",
            description = "타입과 meta 정보를 포함한 요청을 보내면 해당 타입에 따라 상세 로그가 저장됩니다."
    )
    @PostMapping("/event")
    public ResponseEntity<?> event(@RequestBody EventRequestDTO req) {
        Long eventId = logService.handleEvent(req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("event_id", eventId));
    }
}
