package com.poorna.fintech.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.poorna.fintech.Aop.SensitiveLog;
import jakarta.validation.constraints.*;

@Data
public class ChangePasswordRequest {
    @Schema(description = "Current password for identity verification", example = "OldP@ss123", requiredMode = Schema.RequiredMode.REQUIRED)
    @SensitiveLog
    @Size(message="the password Should be with the range of 6 and 20")
    private String oldPassword;
    @Schema(description = "New password to be set for the account", example = "NewP@ss456", requiredMode = Schema.RequiredMode.REQUIRED)
    @SensitiveLog
    @Size(message="the password Should be with the range of 6 and 20")
    private String newPassword;
}
