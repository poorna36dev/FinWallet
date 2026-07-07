package com.poorna.fintech.dtos;

import com.poorna.fintech.Aop.SensitiveLog;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class UserRequest {
    @Schema(description = "Display name of the user", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Name is required")
    private String name;
    @Schema(description = "Email address associated with the user account", example = "john.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Email is required")
    @Email
    private String email;
    @Schema(description = "Password chosen for the user account", example = "P@ssw0rd123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Password is required")
    @Size(message="the password Should be with the range of 6 and 20")
    @SensitiveLog
    private String password;
    @Schema(description = "Unique username used for sign-in", example = "jdoe", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Username is required")
    private String userName;
}
