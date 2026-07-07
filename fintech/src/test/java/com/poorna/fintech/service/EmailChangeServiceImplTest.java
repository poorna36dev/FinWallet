package com.poorna.fintech.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import com.poorna.fintech.dtos.ChangeEmailRequest;
import com.poorna.fintech.entity.EmailVerificationToken;
import com.poorna.fintech.entity.Notification;
import com.poorna.fintech.entity.User;
import com.poorna.fintech.notification.EmailTemplateService;
import com.poorna.fintech.repository.EmailVerificationTokenRepository;
import com.poorna.fintech.repository.NotificatioRepo;
import com.poorna.fintech.repository.UserRepo;

@ExtendWith(MockitoExtension.class)
class EmailChangeServiceImplTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private EmailVerificationTokenRepository emailChangeRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private NotificatioRepo notificationRepo;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private EmailTemplateService emailTemplateService;

    @InjectMocks
    private EmailChangeServiceImpl emailChangeService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailChangeService, "baseUrl", "http://localhost:8080");
    }

    @Test
    void requestEmailChangeCreatesVerificationTokenAndNotification() {
        User user = new User();
        user.setId(1L);
        user.setName("Jane");
        user.setEmail("old@example.com");
        user.setPassword("encoded-old-password");

        ChangeEmailRequest request = new ChangeEmailRequest();
        request.setNewEmail("new@example.com");
        request.setPassword("plain-password");

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plain-password", "encoded-old-password")).thenReturn(true);
        when(userRepo.existsByEmail("new@example.com")).thenReturn(false);
        when(emailChangeRepo.findByUserId(1L)).thenReturn(Optional.empty());
        when(emailChangeRepo.save(any(EmailVerificationToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(notificationRepo.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(emailTemplateService.handleEmailChangeVerification(any(String.class), any(String.class)))
                .thenReturn("<html>change email</html>");

        emailChangeService.requestEmailChange(1L, request);

        verify(emailChangeRepo).save(any(EmailVerificationToken.class));
        verify(notificationRepo).save(any(Notification.class));
        verify(eventPublisher).publishEvent(any(Notification.class));
    }
}
