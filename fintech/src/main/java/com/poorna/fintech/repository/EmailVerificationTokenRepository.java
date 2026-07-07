package com.poorna.fintech.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poorna.fintech.entity.EmailVerificationToken;


public interface EmailVerificationTokenRepository
        extends JpaRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken> findByToken(String token);

    void deleteByUserId(Long userId);

    Optional<EmailVerificationToken> findByUserId(Long userId);
}
