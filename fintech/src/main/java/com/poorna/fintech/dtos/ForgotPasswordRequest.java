package com.poorna.fintech.dtos;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class ForgotPasswordRequest {
    @Email
    @Schema(description = "email to verify the account")
    @NotBlank
    private String email;
}
