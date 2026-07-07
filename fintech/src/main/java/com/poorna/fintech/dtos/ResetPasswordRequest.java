package com.poorna.fintech.dtos;

import com.poorna.fintech.Aop.SensitiveLog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
public class ResetPasswordRequest {
    @Schema(description = "new password for reset", example = "P@ssw0rd123", requiredMode = Schema.RequiredMode.REQUIRED)
    @jakarta.validation.constraints.NotBlank
    @jakarta.validation.constraints.Size(min = 6, message = "Password must be at least 6 characters long")
    @SensitiveLog
    private String newPassword;
    @Schema(description = "confirm new password for reset", example = "P@ssw0rd123", requiredMode = Schema.RequiredMode.REQUIRED)
    @jakarta.validation.constraints.NotBlank
    @jakarta.validation.constraints.Size(min = 6, message = "Password must be at least 6 characters long")
    @SensitiveLog
    private String confirmNewPassword;
}
