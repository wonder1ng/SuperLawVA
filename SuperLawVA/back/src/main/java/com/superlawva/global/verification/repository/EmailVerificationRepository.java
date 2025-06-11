package com.superlawva.global.verification.repository;

import com.superlawva.global.verification.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailVerificationRepository
        extends JpaRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken> findByEmailAndCode(String email, String code);

    boolean existsByEmailAndVerifiedIsTrue(String email);

    void deleteByExpiresAtBefore(LocalDateTime time);
}
