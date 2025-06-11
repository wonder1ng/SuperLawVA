package com.study.MyLilLawyer.domain.log.action.controller;

import com.study.MyLilLawyer.domain.log.action.dto.EventRequestDTO;
import com.study.MyLilLawyer.domain.log.action.dto.PageViewRequestDTO;
import com.study.MyLilLawyer.domain.log.action.dto.SessionRequestDTO;
import com.study.MyLilLawyer.domain.log.action.dto.*;
import com.study.MyLilLawyer.domain.log.action.service.LogService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/log")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    /* 세션 */
    @PostMapping("/session")
    public ResponseEntity<?> session(@RequestBody SessionRequestDTO req) {
        Long id = logService.handleSession(req);
        return "start".equals(req.action())
                ? ResponseEntity.status(HttpStatus.CREATED).body(Map.of("session_id", id))
                : ResponseEntity.noContent().build();
    }

    /* 페이지뷰 */
    @PostMapping("/pageview")
    public ResponseEntity<?> pageview(@RequestBody PageViewRequestDTO req) {
        Long id = logService.handlePageView(req);
        return "start".equals(req.action())
                ? ResponseEntity.status(HttpStatus.CREATED).body(Map.of("view_id", id))
                : ResponseEntity.noContent().build();
    }

    /* 이벤트 */
    @PostMapping("/event")
    public ResponseEntity<?> event(@RequestBody EventRequestDTO req) {
        Long id = logService.handleEvent(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("event_id", id));
    }
}
