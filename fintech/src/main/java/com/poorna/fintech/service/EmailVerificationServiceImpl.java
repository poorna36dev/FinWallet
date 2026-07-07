package com.poorna.fintech.service;


import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.poorna.fintech.entity.EmailVerificationToken;
import com.poorna.fintech.entity.Notification;
import com.poorna.fintech.entity.User;
import com.poorna.fintech.entity.NotificationStatus;
import com.poorna.fintech.entity.NotificationType;
import com.poorna.fintech.exception.BadRequestException;
import com.poorna.fintech.repository.EmailVerificationTokenRepository;
import com.poorna.fintech.repository.NotificatioRepo;
import com.poorna.fintech.repository.UserRepo;
import com.poorna.fintech.notification.dto.Recipient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import com.poorna.fintech.notification.EmailTemplateService;
import org.springframework.transaction.annotation.Transactional;
import com.poorna.fintech.entity.VerificationPurpose;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepo;
    private final NotificatioRepo notificationRepo;
    private final UserRepo userRepo;
    private final ApplicationEventPublisher eventPublisher;
    private final EmailTemplateService emailTemplateService;
    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    @Transactional
    public void sendVerificationEmail(User user,VerificationPurpose purpose,String targetEmail) {

        tokenRepo.findByUserId(user.getId())
        .ifPresent(tokenRepo::delete);

        EmailVerificationToken token = EmailVerificationToken.builder()
                .user(user)
                .purpose(purpose)
                .build();

        tokenRepo.save(token);
        String link = baseUrl + "/auth/verify-email?token=" + token.getToken();
        Notification notification = new Notification();
        notification.setRecipient(new Recipient(user.getEmail(),user.getName()));
        notification.setSubject("Verify your Email");
        notification.setBody(emailTemplateService.handleVerificationEmail(user.getName(),link));
        notification.setType(NotificationType.EMAIL);
        notification.setStatus(NotificationStatus.PENDING);
        notificationRepo.save(notification);
        eventPublisher.publishEvent(notification);
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {

        EmailVerificationToken verificationToken = tokenRepo.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid verification link"));

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Verification link expired");
        }

        User user = verificationToken.getUser();

        switch(verificationToken.getPurpose()) {

            case VerificationPurpose.REGISTRATION -> {
                user.setEmailVerified(true);
            }

            case VerificationPurpose.EMAIL_CHANGE -> {
                user.setEmail(verificationToken.getPendingEmail());
                user.setEmailVerified(true);
            }
        }

        userRepo.save(user);

        tokenRepo.delete(verificationToken);
    }
}