package com.poorna.fintech.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.poorna.fintech.dtos.LoginRequest;
import com.poorna.fintech.dtos.UserRequest;
import com.poorna.fintech.entity.User;
import com.poorna.fintech.entity.VerificationPurpose;
import com.poorna.fintech.security.JwtUtil;
import com.poorna.fintech.service.EmailVerificationService;
import com.poorna.fintech.service.PasswordResetService;
import com.poorna.fintech.service.UserService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    @Mock
    private EmailVerificationService emailVerificationService;

    @Mock
    private PasswordResetService passwordResetService;

    @InjectMocks
    private AuthController authController;

    @Test
    void loginReturnsAuthenticationFailureReasonWhenCredentialsAreInvalid() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUserName("jdoe");
        request.setPassword("wrong-password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException(""));

        ResponseEntity<?> response = authController.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isInstanceOf(Map.class);
        assertThat(((Map<?, ?>) response.getBody()).get("error")).isEqualTo("Authentication failed");
    }

    @Test
    void registerUserCreatesUserAndSendsVerificationEmail() {
        UserRequest request = new UserRequest();
        request.setName("Jane Doe");
        request.setEmail("jane@example.com");
        request.setUserName("jdoe");
        request.setPassword("secret123");

        User createdUser = new User();
        createdUser.setId(7L);
        createdUser.setEmail("jane@example.com");
        createdUser.setUserName("jdoe");

        when(userService.createUser("Jane Doe", "jane@example.com", "jdoe", "secret123"))
                .thenReturn(createdUser);

        ResponseEntity<?> response = authController.registerUser(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(createdUser);
        verify(userService).createUser("Jane Doe", "jane@example.com", "jdoe", "secret123");
        verify(emailVerificationService).sendVerificationEmail(createdUser, VerificationPurpose.REGISTRATION, "jane@example.com");
    }
}
