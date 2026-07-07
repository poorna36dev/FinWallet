package com.poorna.fintech.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import com.poorna.fintech.Aop.SensitiveLog;

@Data
public class ChangeEmailRequest {

    @Email
    @NotBlank
    private String newEmail;

    @NotBlank
    @SensitiveLog
    private String password;
}