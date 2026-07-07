package com.poorna.fintech.dtos;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WalletResponse implements Serializable {
    private static final long  serialVersionUID = 1L;
    @Schema(description = "Unique identifier of the wallet", example = "101")
    private long id;
    @Schema(description = "Friendly name assigned to the wallet", example = "Primary Wallet")
    private String name;
    @Schema(description = "Current available balance in the wallet", example = "250.75")
    private BigDecimal balance;
    @Schema(description = "Currency code used by the wallet", example = "USD")
    private String currency;
}
