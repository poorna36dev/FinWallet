package com.poorna.fintech.dtos;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WalletRequest {
    @Schema(description = "Friendly name for the wallet", example = "Primary Wallet", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Name is required")
    private String name;
    @Schema(description = "Currency code used by the wallet", example = "USD", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Currency is required")
    private String currency;
}
