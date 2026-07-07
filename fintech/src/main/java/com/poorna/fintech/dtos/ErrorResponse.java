package com.poorna.fintech.dtos;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    @Schema(description = "Timestamp when the error was generated", example = "2026-07-05T10:15:30")
    private LocalDateTime timestamp;
    @Schema(description = "HTTP status code returned for the error", example = "404")
    private int status;
    @Schema(description = "Human-readable error message describing the failure", example = "Wallet not found")
    private String message;

}
