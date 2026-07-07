package com.poorna.fintech.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.poorna.fintech.dtos.ResetPasswordRequest;
import com.poorna.fintech.entity.Notification;
import com.poorna.fintech.entity.PasswordResetToken;
import com.poorna.fintech.entity.User;
import com.poorna.fintech.notification.EmailTemplateService;
import com.poorna.fintech.repository.NotificatioRepo;
import com.poorna.fintech.repository.PasswordResetRepo;
import com.poorna.fintech.repository.UserRepo;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private PasswordResetRepo passwordResetRepo;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private NotificatioRepo notificationRepo;

    @Mock
    private EmailTemplateService emailTemplateService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private PasswordResetService passwordResetService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(passwordResetService, "baseUrl", "http://localhost:8080");
    }

    @Test
    void sendPasswordResetMailCreatesTokenAndPublishesNotification() {
        User user = new User();
        user.setId(1L);
        user.setName("Jane");
        user.setEmail("jane@example.com");

        when(passwordResetRepo.findByUserId(1L)).thenReturn(Optional.empty());
        when(passwordResetRepo.save(any(PasswordResetToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(notificationRepo.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(emailTemplateService.handlePasswordResetEmail(any(String.class), any(String.class)))
                .thenReturn("<html>reset</html>");

        passwordResetService.sendPasswordResetMail(user);

        verify(passwordResetRepo).save(any(PasswordResetToken.class));
        verify(notificationRepo).save(any(Notification.class));
        verify(eventPublisher).publishEvent(any(Notification.class));
    }

    @Test
    void passwordResetUpdatesPasswordAndSendsConfirmation() {
        User user = new User();
        user.setPassword("old-hash");
        user.setName("Jane");
        user.setEmail("jane@example.com");

        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(10));

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setNewPassword("new-password");
        request.setConfirmNewPassword("new-password");

        when(passwordResetRepo.findByToken("token")).thenReturn(Optional.of(token));
        when(passwordEncoder.matches("new-password", "old-hash")).thenReturn(false);
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-new-password");
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(notificationRepo.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        passwordResetService.PasswordReset(request, "token");

        assertThat(user.getPassword()).isEqualTo("encoded-new-password");
        verify(userRepo).save(user);
        verify(notificationRepo).save(any(Notification.class));
        verify(eventPublisher).publishEvent(any(Notification.class));
    }
}
