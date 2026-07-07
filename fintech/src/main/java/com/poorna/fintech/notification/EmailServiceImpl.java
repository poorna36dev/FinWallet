package com.poorna.fintech.notification;


import com.poorna.fintech.notification.dto.SendEmailRequest;
import org.springframework.web.client.RestClient;
import lombok.RequiredArgsConstructor;
import com.poorna.fintech.config.BrevoProperties;
import com.poorna.fintech.notification.dto.Sender;
import com.poorna.fintech.repository.NotificatioRepo;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import com.poorna.fintech.entity.Notification;
import com.poorna.fintech.entity.NotificationStatus;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import com.poorna.fintech.Aop.BusinessLogger;
import com.poorna.fintech.service.MetricsService;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final MetricsService metricsService;    
    private final BusinessLogger businessLogger;    
    private final RestClient brevoRestClient;
    private final NotificatioRepo notificationRepository;
    private final BrevoProperties brevoProperties;

    @Override
    public void sendEmail(Notification notification) {

        SendEmailRequest request = new SendEmailRequest();

        request.setSender(
                new Sender(
                        brevoProperties.getSenderName(),
                        brevoProperties.getSenderEmail()));

        request.setRecipients(
                List.of(notification.getRecipient()));

        request.setSubject(
                notification.getSubject());

        request.setHtmlContent(
                notification.getBody());

        try {

            brevoRestClient.post()
                    .uri("/smtp/email")
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();

            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            businessLogger.emailSent(
                        notification.getId(),
                        notification.getRecipient().getEmail()
                );
            metricsService.emailSent();

        } catch (Exception ex) {
                businessLogger.emailFailed(
                        notification.getId(),
                        notification.getRecipient().getEmail(),
                        notification.getRetryCount()
                );
                metricsService.emailFailure();                
            if(notification.getRetryCount()>=4){
                notification.setStatus(NotificationStatus.PERMANENTLY_FAILED);
            }   
            else
            {
            notification.setStatus(NotificationStatus.FAILED);

            notification.setRetryCount(
                    notification.getRetryCount() + 1);

            notification.setNextRetryAt(
                    LocalDateTime.now().plusMinutes(5));

            log.error("Failed to send email", ex);
            }
        } finally {

            notificationRepository.save(notification);

        }

    }

}