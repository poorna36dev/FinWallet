package com.poorna.fintech.dtos;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TransferRequest {
    @Schema(description = "Identifier of the wallet sending the funds", example = "101", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Source wallet ID is required")
    @Positive
    private long SourceWalletId;
    @Schema(description = "Identifier of the wallet receiving the funds", example = "202", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Destination wallet ID is required")
    @Positive
    private long DestinationWalletId;
    @Schema(description = "Amount to transfer", example = "150.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Amount is required")
    @Positive(message = "Amount must be a positive value")
    @DecimalMin("0.01")
    private BigDecimal Amount;
}
