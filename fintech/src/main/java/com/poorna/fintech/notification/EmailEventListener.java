package com.poorna.fintech.notification;

import org.springframework.stereotype.Component;

import com.poorna.fintech.dtos.TransferCompletedEvent;
import com.poorna.fintech.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.scheduling.annotation.Async;

import com.poorna.fintech.entity.Notification;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailEventListener {

    private final EmailTemplateService templateService;
    private final NotificationService notificationService;
    private final EmailService emailService;

    @Async
    @TransactionalEventListener(phase=TransactionPhase.AFTER_COMMIT)
    public void handleEmail(TransferCompletedEvent event) {

        String htmlBody =
                templateService.buildTransferSuccessEmail(event);

        Notification notification =
                notificationService.saveNotification(
                        htmlBody,
                        event,
                        "Transfer Successful");

        emailService.sendEmail(notification);
    }

}
