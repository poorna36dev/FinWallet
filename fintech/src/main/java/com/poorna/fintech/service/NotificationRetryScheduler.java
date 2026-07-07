package com.poorna.fintech.service;

import com.poorna.fintech.notification.EmailService;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.poorna.fintech.repository.NotificatioRepo;
import com.poorna.fintech.entity.Notification;
import com.poorna.fintech.entity.NotificationStatus;
import com.poorna.fintech.Aop.BusinessLogger;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationRetryScheduler {
    
    private final EmailService emailService;
    private final NotificatioRepo notificationRepo;
    private final BusinessLogger businessLogger;
    private final AtomicBoolean running = new AtomicBoolean(false);

    private static final int BATCH_SIZE = 100;

    @Scheduled(fixedRate = 300000)
    public void retryFailedEmails() {
        if (!running.compareAndSet(false, true)) {
            log.info("Notification scheduler is already running.");
            return;
        }
        try{
        while (true) {

            Page<Notification> page =
                    notificationRepo.findNotifications(
                            NotificationStatus.FAILED,
                            5,
                            LocalDateTime.now(),
                            PageRequest.of(0, BATCH_SIZE));

            if (page.isEmpty()) {
                break;
            }

            for (Notification notification : page.getContent()) {
            try {

                emailService.sendEmail(notification);
                businessLogger.notificationRetry(notification.getId(),notification.getRetryCount());

            } catch (Exception ex) {

                log.error("Failed to process notification {}", notification.getId(), ex);
            }
                
            }
        }
        }
        finally{
            running.set(false);
        }
    }
}
