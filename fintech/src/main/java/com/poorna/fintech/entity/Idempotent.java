package com.poorna.fintech.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "idempotent")
@Data
public class Idempotent{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="idempotent_key", unique = true)
    @NotNull
    private String idempotentKey;
    @Column(name = "request_hash")
    private String requestHash;
    @Enumerated
    @Column(name = "status")
    private Status status;
    @Column(name = "transaction_id")
    private long transactionId;
    @CreationTimestamp
    private LocalDateTime createdAt;

}