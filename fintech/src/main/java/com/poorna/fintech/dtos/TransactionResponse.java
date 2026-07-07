package com.poorna.fintech.dtos;

import java.io.Serializable;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TransactionResponse implements Serializable{
    @Schema(description = "Unique identifier of the transaction", example = "5001")
    private long id;
    @Schema(description = "Type of transaction performed", example = "TRANSFER")
    private String type;
    @Schema(description = "Current status of the transaction", example = "COMPLETED")
    private String status;
    @Schema(description = "Currency used for the transaction", example = "USD")
    private String currency;
    @Schema(description = "Monetary amount involved in the transaction", example = "150.00")
    @Positive
    private java.math.BigDecimal amount;
}
