package com.poorna.fintech.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransferCompletedEvent {

    private final Long transactionId;

    private final Long sourceWalletId;

    private final Long destinationWalletId;

    private final String username;

    private final String name;

    private final String email;

    private final BigDecimal amount;

    private final String currency;

    private final LocalDateTime transferredAt;

}