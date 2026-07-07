package com.poorna.fintech.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poorna.fintech.entity.Idempotent;

public interface IdempotentRepo extends JpaRepository<Idempotent, Long> {
    Optional<Idempotent> findByIdempotentKey(String idempotentKey);
    
}
