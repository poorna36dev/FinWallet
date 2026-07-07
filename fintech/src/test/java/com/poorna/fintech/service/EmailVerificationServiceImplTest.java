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
import org.springframework.test.util.ReflectionTestUtils;

import com.poorna.fintech.entity.EmailVerificationToken;
import com.poorna.fintech.entity.Notification;
import com.poorna.fintech.entity.User;
import com.poorna.fintech.entity.VerificationPurpose;
import com.poorna.fintech.notification.EmailTemplateService;
import com.poorna.fintech.repository.EmailVerificationTokenRepository;
import com.poorna.fintech.repository.NotificatioRepo;
import com.poorna.fintech.repository.UserRepo;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceImplTest {

    @Mock
    private EmailVerificationTokenRepository tokenRepo;

    @Mock
    private NotificatioRepo notificationRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private EmailTemplateService emailTemplateService;

    @InjectMocks
    private EmailVerificationServiceImpl emailVerificationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailVerificationService, "baseUrl", "http://localhost:8080");
    }

    @Test
    void sendVerificationEmailCreatesTokenAndPublishesNotification() {
        User user = new User();
        user.setId(1L);
        user.setName("Jane");
        user.setEmail("jane@example.com");

        when(tokenRepo.findByUserId(1L)).thenReturn(Optional.empty());
        when(tokenRepo.save(any(EmailVerificationToken.class))).thenAnswer(invocation -> {
            EmailVerificationToken token = invocation.getArgument(0);
            token.setToken("generated-token");
            return token;
        });
        when(notificationRepo.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(emailTemplateService.handleVerificationEmail("Jane", "http://localhost:8080/auth/verify-email?token=generated-token"))
                .thenReturn("<html>verify</html>");

        emailVerificationService.sendVerificationEmail(user, VerificationPurpose.REGISTRATION, "jane@example.com");

        verify(tokenRepo).save(any(EmailVerificationToken.class));
        verify(notificationRepo).save(any(Notification.class));
        verify(eventPublisher).publishEvent(any(Notification.class));
    }

    @Test
    void verifyEmailMarksUserAsVerified() {
        User user = new User();
        user.setEmailVerified(false);

        EmailVerificationToken token = new EmailVerificationToken();
        token.setUser(user);
        token.setPurpose(VerificationPurpose.REGISTRATION);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(10));

        when(tokenRepo.findByToken("abc")).thenReturn(Optional.of(token));
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        emailVerificationService.verifyEmail("abc");

        assertThat(user.getEmailVerified()).isTrue();
        verify(userRepo).save(user);
        verify(tokenRepo).delete(token);
    }
}
