package com.poorna.fintech.dtos;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class WithdrawRequest {
    @Schema(description = "Identifier of the wallet from which funds will be withdrawn", example = "101", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "wallet ID is required")
    @Positive
    private long walletId;
    @Schema(description = "Amount to withdraw from the wallet", example = "75.50", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "amount is required")
    @Positive(message = "Amount must be a positive value")
    @DecimalMin("0.01")
    private BigDecimal amount;
}
