package com.poorna.fintech.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.poorna.fintech.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.poorna.fintech.service.UserDetailsImpl;
import com.poorna.fintech.dtos.ErrorResponse;
import lombok.RequiredArgsConstructor;
import com.poorna.fintech.dtos.LoginRequest;
import com.poorna.fintech.dtos.UserRequest;
import com.poorna.fintech.security.JwtUtil;
import com.poorna.fintech.service.UserService;
import com.poorna.fintech.service.EmailVerificationService;
import com.poorna.fintech.dtos.ForgotPasswordRequest;
import com.poorna.fintech.service.PasswordResetService;
import com.poorna.fintech.entity.VerificationPurpose;
import com.poorna.fintech.exception.EmailNotVerifiedException;
import com.poorna.fintech.dtos.ResetPasswordRequest;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for authenticating users and creating new accounts")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final EmailVerificationService emailVerificationService;
    private final PasswordResetService passwordResetService;


    @Operation(
        summary = "Authenticate a user",
        description = "Validates user credentials and issues a JWT token for subsequent authenticated requests."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Authentication succeeded and a JWT token was returned"),
        @ApiResponse(responseCode = "401", description = "The supplied credentials are invalid", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUserName(),
                        request.getPassword()));

        User user = ((UserDetailsImpl)auth.getPrincipal()).getUser();

        if (!user.getEmailVerified()) {
            throw new EmailNotVerifiedException(
                    "Email not verified. Please verify your email.");
        }

        String token = jwtUtil.generateToken(request.getUserName());

        return ResponseEntity.ok(Map.of("token", token));
    }

    @Operation(
        summary = "Register a new user",
        description = "Creates a new application user account with the provided profile information."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User account was created successfully"),
        @ApiResponse(responseCode = "400", description = "The supplied registration payload is invalid", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("register/user")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRequest userRequest) {
        User createdUser = userService.createUser(userRequest.getName(), userRequest.getEmail(),userRequest.getUserName(), userRequest.getPassword());
        emailVerificationService.sendVerificationEmail(createdUser,VerificationPurpose.REGISTRATION,userRequest.getEmail());
        return ResponseEntity.created(null).body(createdUser);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@Validated @RequestParam String token){
        emailVerificationService.verifyEmail(token);
        return ResponseEntity.ok("Email Verified Successfully");
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request){
        User user=userService.findByEmailForPasswordReset(request.getEmail());
        passwordResetService.sendPasswordResetMail(user);
        return ResponseEntity.ok("{\r\n" + //
                                "    \"message\": \"If an account with that email exists, a password reset link has been sent.\"\r\n" + //
                                "}");
    }

    @PostMapping("/password-reset")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request,@RequestParam String token){
        passwordResetService.PasswordReset(request,token);
        return ResponseEntity.ok("{\r\n" + //
                                "    \"message\": \"The Password Has been reseted . Login with New Password.\"\r\n" + //
                                "}");
    }

    

}
