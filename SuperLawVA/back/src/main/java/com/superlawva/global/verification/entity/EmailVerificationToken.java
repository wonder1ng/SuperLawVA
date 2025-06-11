package com.superlawva.global.verification.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVerificationToken {

    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 6, nullable = false)
    private String code;            // 숫자 6자리

    private LocalDateTime expiresAt;

    private boolean verified;

    /* ---------- 팩터리 ---------- */

    public static EmailVerificationToken issue(String email, Duration ttl) {
        EmailVerificationToken t = new EmailVerificationToken();
        t.email = email;
        t.code  = String.format("%06d", new Random().nextInt(1_000_000));
        t.expiresAt = LocalDateTime.now().plus(ttl);
        t.verified  = false;
        return t;
    }

    public void markVerified() { this.verified = true; }
}
