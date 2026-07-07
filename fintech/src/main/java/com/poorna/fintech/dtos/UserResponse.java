package com.poorna.fintech.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserResponse implements java.io.Serializable {
    @Schema(description = "Unique identifier of the user", example = "1")
    private long id;
    @Schema(description = "Unique username for the account", example = "jdoe")
    private String userName;
    @Schema(description = "Display name of the user", example = "John Doe")
    private String name;
    @Schema(description = "Email address associated with the account", example = "john.doe@example.com")
    private String email;
    @Schema(description = "Wallet details associated with the user account")
    private WalletResponse walletResponse;

}
