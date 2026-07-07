package com.poorna.fintech.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poorna.fintech.dtos.ChangeEmailRequest;
import com.poorna.fintech.entity.EmailVerificationToken;
import com.poorna.fintech.entity.Notification;
import com.poorna.fintech.entity.NotificationStatus;
import com.poorna.fintech.entity.NotificationType;
import com.poorna.fintech.entity.User;
import com.poorna.fintech.exception.BadRequestException;
import com.poorna.fintech.exception.UserNotFoundException;
import com.poorna.fintech.notification.EmailTemplateService;
import com.poorna.fintech.notification.dto.Recipient;
import com.poorna.fintech.repository.EmailVerificationTokenRepository;
import com.poorna.fintech.repository.NotificatioRepo;
import com.poorna.fintech.repository.UserRepo;
import com.poorna.fintech.entity.VerificationPurpose;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class EmailChangeServiceImpl implements EmailChangeService {

    private final UserRepo userRepo;
    private final EmailVerificationTokenRepository emailChangeRepo;
    private final PasswordEncoder passwordEncoder;
    private final NotificatioRepo notificationRepo;
    private final ApplicationEventPublisher eventPublisher;
    private final EmailTemplateService emailTemplateService;

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    @Transactional
    public void requestEmailChange(long userId,
                                   ChangeEmailRequest request) {

        User user = userRepo.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(),
                user.getPassword())) {

            throw new BadRequestException("Incorrect password");
        }

        if (userRepo.existsByEmail(request.getNewEmail())) {

            throw new BadRequestException("Email already exists");
        }

        if (user.getEmail().equalsIgnoreCase(request.getNewEmail())) {

            throw new BadRequestException(
                    "New email must be different from current email");
        }

        emailChangeRepo.findByUserId(user.getId())
                .ifPresent(emailChangeRepo::delete);

        emailChangeRepo.flush();

        EmailVerificationToken token = EmailVerificationToken.builder()
                .user(user)
                .pendingEmail(request.getNewEmail())
                .purpose(VerificationPurpose.EMAIL_CHANGE)
                .build();

        emailChangeRepo.save(token);

        String link = baseUrl +
                "/auth/verify-email?token=" +
                token.getToken();

        Notification notification = new Notification();

        notification.setRecipient(
                new Recipient(request.getNewEmail(), user.getName()));

        notification.setSubject("Verify Your New Email Address");

        notification.setBody(
                emailTemplateService.handleEmailChangeVerification(
                        user.getName(),
                        link));

        notification.setType(NotificationType.EMAIL);
        notification.setStatus(NotificationStatus.PENDING);

        notificationRepo.save(notification);

        eventPublisher.publishEvent(notification);
    }

    
}