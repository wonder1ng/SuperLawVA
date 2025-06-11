package com.superlawva.domain.user.controller;

import com.superlawva.global.verification.dto.request.*;
import com.superlawva.global.verification.service.EmailVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService svc;

    @PostMapping("/send")
    public ResponseEntity<Void> send(@Valid @RequestBody EmailSendRequestDTO req) {
        svc.send(req.getEmail());
        return ResponseEntity.ok().build();          // ✅ ApiResponse → Void 로 단순화
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verify(@Valid @RequestBody EmailVerifyRequestDTO req) {
        svc.verify(req.getEmail(), req.getCode());
        return ResponseEntity.ok().build();
    }
}
