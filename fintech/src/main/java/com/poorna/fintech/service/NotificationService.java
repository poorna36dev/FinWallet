package com.poorna.fintech.service;

import org.springframework.stereotype.Service;
import com.poorna.fintech.dtos.TransferCompletedEvent;
import com.poorna.fintech.entity.NotificationStatus;
import com.poorna.fintech.entity.NotificationType;
import com.poorna.fintech.notification.dto.Recipient;
import com.poorna.fintech.repository.NotificatioRepo;
import lombok.RequiredArgsConstructor;
import com.poorna.fintech.entity.Notification;

@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificatioRepo notificationRepo;

    public Notification saveNotification(String htmlBody,TransferCompletedEvent event,String Subject){
        Notification notification = new Notification();
        notification.setBody(htmlBody);
        notification.setType(NotificationType.EMAIL);
        notification.setRecipient(new Recipient(event.getEmail(), event.getName()));
        notification.setStatus(NotificationStatus.PENDING);
        notification.setSubject("Transfer Successful");
        notification.setNextRetryAt(null);
        notification.setSentAt(null);
        return notificationRepo.save(notification);
    }
    
}
