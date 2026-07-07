package com.poorna.fintech.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poorna.fintech.dtos.ChangeEmailRequest;
import com.poorna.fintech.dtos.ChangePasswordRequest;
import com.poorna.fintech.dtos.TransactionResponse;
import com.poorna.fintech.dtos.UserRequest;
import com.poorna.fintech.dtos.UserResponse;
import com.poorna.fintech.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import com.poorna.fintech.dtos.ErrorResponse;
import com.poorna.fintech.service.EmailChangeService;
import com.poorna.fintech.service.UserDetailsImpl;
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for managing user profiles, credentials, and account state")
public class UserController {
    private final UserService userService;
    private final EmailChangeService emailVerificationService;


    @Operation(
        summary = "List all users",
        description = "Returns the full list of registered users. This endpoint is restricted to administrators."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User list retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "The caller is not authorized to access this resource", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user")
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @Operation(
        summary = "Retrieve authenticated user transactions",
        description = "Returns the transaction history for the currently authenticated user."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transaction history retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication is required to access this resource", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("wallets/transactions")
    public ResponseEntity<List<TransactionResponse>> getUserTransactions(Authentication principal) {
        List<TransactionResponse> transactions = userService.getUserTransactions(Long.parseLong(principal.getName()));
        return ResponseEntity.ok(transactions);
    }

    @Operation(
        summary = "Get the current user profile",
        description = "Returns the profile details for the authenticated user."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User profile retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "The requested user profile does not exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/profile")
    public ResponseEntity<?> getUser(Authentication principal) {
        long userId = Long.parseLong(principal.getName());
        UserResponse user = userService.getUser(userId);
        return ResponseEntity.ok(user);
    }

    @Operation(
        summary = "Update the current user profile",
        description = "Updates the authenticated user's profile information with the provided values."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User profile updated successfully"),
        @ApiResponse(responseCode = "400", description = "The supplied profile data is invalid", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/profile")
    public ResponseEntity<?> updateUser(Authentication principal,@Valid @RequestBody UserRequest userRequest) {
        long userId = Long.parseLong(principal.getName());
        UserResponse updatedUser = userService.updateUser(userId, userRequest);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(
        summary = "Change the current user's password",
        description = "Updates the authenticated user's password after validating the current password."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Password updated successfully"),
        @ApiResponse(responseCode = "400", description = "The supplied password change request is invalid", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/profile/password")
    public ResponseEntity<?> updatePassword(Authentication principal,
        @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        long userId = Long.parseLong(principal.getName());
        UserResponse updatedUser = userService.updatePassword(userId, changePasswordRequest.getOldPassword(),
                changePasswordRequest.getNewPassword());
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(
        summary = "Deactivate the current user account",
        description = "Marks the authenticated user's account as deactivated."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User account deactivated successfully"),
        @ApiResponse(responseCode = "404", description = "The requested user account does not exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/profile/deactivate")
    public ResponseEntity<?> deactivateUser(Authentication principal) {
        long userId = Long.parseLong(principal.getName());
        UserResponse updatedUser = userService.deactivateUser(userId);
        return ResponseEntity.ok(updatedUser);
    }
    @PostMapping("/change-email")
    public ResponseEntity<?> changeEmail(
        Authentication authentication,
        @Valid @RequestBody ChangeEmailRequest request) {

            UserDetailsImpl principal =
        (UserDetailsImpl) authentication.getPrincipal();

        long userId=principal.getUser().getId();

            emailVerificationService.requestEmailChange(userId, request);

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Verification email has been sent to your new email address. Please verify it to complete the email change."
                    )
            );
        }
}
