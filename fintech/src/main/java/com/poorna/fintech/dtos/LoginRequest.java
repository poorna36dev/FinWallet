package com.poorna.fintech.dtos;

import com.poorna.fintech.Aop.SensitiveLog;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class LoginRequest {
    @Schema(description = "Unique username used to authenticate the user", example = "jdoe", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String userName;
    @Schema(description = "Account password for authentication", example = "P@ssw0rd123", requiredMode = Schema.RequiredMode.REQUIRED)
    @jakarta.validation.constraints.NotBlank
    @jakarta.validation.constraints.Size(min = 6, message = "Password must be at least 6 characters long")
    @SensitiveLog
    private String password;
}
