package com.poorna.fintech.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import com.poorna.fintech.entity.PasswordResetToken;
import com.poorna.fintech.entity.NotificationStatus;
import com.poorna.fintech.entity.NotificationType;
import com.poorna.fintech.notification.EmailTemplateService;
import com.poorna.fintech.notification.dto.Recipient;
import com.poorna.fintech.repository.PasswordResetRepo;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.poorna.fintech.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import com.poorna.fintech.exception.BadRequestException;
import com.poorna.fintech.entity.Notification;
import com.poorna.fintech.entity.User;
import com.poorna.fintech.repository.NotificatioRepo;
import java.time.LocalDateTime;
import com.poorna.fintech.dtos.ResetPasswordRequest;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final PasswordResetRepo passwordResetRepo;
    private final ApplicationEventPublisher eventPublisher;
    @Value("${app.base-url}")
    private String baseUrl;
    private final NotificatioRepo notificationRepo;
    private final EmailTemplateService emailTemplateService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;

    @Transactional
    public void sendPasswordResetMail(User user) {

        passwordResetRepo.findByUserId(user.getId())
        .ifPresent(passwordResetRepo::delete);
        passwordResetRepo.flush();
        PasswordResetToken token = PasswordResetToken.builder()
                .user(user)
                .build();

        passwordResetRepo.save(token);
        String link = baseUrl + "/auth/password-reset?token=" + token.getToken();
        Notification notification = new Notification();
        notification.setRecipient(new Recipient(user.getEmail(),user.getName()));
        notification.setSubject("Verify your Email");
        notification.setBody(emailTemplateService.handlePasswordResetEmail(user.getName(),link));
        notification.setType(NotificationType.EMAIL);
        notification.setStatus(NotificationStatus.PENDING);
        notificationRepo.save(notification);
        eventPublisher.publishEvent(notification);
    }
    @Transactional
    public void PasswordReset(ResetPasswordRequest request,String token) {
        PasswordResetToken passwordResetToken=passwordResetRepo.findByToken(token)
                        .orElseThrow(()-> new BadRequestException("invalid Token"));
        if (passwordResetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("token expired");
        }
        User user=passwordResetToken.getUser();
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new BadRequestException("Passwords do not match");
        }
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BadRequestException(
                "New password must be different from your current password");
        }
        
        String encoded = passwordEncoder.encode(request.getNewPassword());

        user.setPassword(encoded);
        userRepo.save(user);

        System.out.println("Saved Hash: " + encoded);
        System.out.println(
            passwordEncoder.matches(request.getNewPassword(), encoded)
        );
        passwordResetRepo.delete(passwordResetToken);
        Notification notification = new Notification();
        notification.setRecipient(new Recipient(user.getEmail(),user.getName()));
        notification.setSubject("Password Reset Successful");
        notification.setBody(emailTemplateService.handlePasswordReset(user.getName()));
        notification.setType(NotificationType.EMAIL);
        notification.setStatus(NotificationStatus.PENDING);
        notificationRepo.save(notification);
        eventPublisher.publishEvent(notification); 
    }
}
