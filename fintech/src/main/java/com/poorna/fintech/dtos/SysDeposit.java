package com.poorna.fintech.dtos;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SysDeposit {
    @Schema(description = "Identifier of the wallet receiving the deposit", example = "101", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Wallet ID cannot be null")
    @NotBlank(message = "Wallet ID is required")
    @Positive
    private long walletId;
    @Schema(description = "Amount to deposit into the wallet", example = "500.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Amount is required")
    @Positive(message = "Amount must be a positive value")
    @DecimalMin("0.01")
    private BigDecimal amount;
}
