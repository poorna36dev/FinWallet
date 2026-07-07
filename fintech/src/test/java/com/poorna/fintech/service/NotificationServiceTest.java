package com.poorna.fintech.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.poorna.fintech.dtos.TransferCompletedEvent;
import com.poorna.fintech.entity.Notification;
import com.poorna.fintech.entity.NotificationStatus;
import com.poorna.fintech.entity.NotificationType;
import com.poorna.fintech.repository.NotificatioRepo;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificatioRepo notificationRepo;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void saveNotificationPersistsNotificationWithTransferDetails() {
        TransferCompletedEvent event = new TransferCompletedEvent(
                1L,
                2L,
                3L,
                "jdoe",
                "Jane",
                "jane@example.com",
                java.math.BigDecimal.TEN,
                "USD",
                java.time.LocalDateTime.now());

        when(notificationRepo.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Notification notification = notificationService.saveNotification("<html>body</html>", event, "Transfer Successful");

        assertThat(notification.getBody()).isEqualTo("<html>body</html>");
        assertThat(notification.getType()).isEqualTo(NotificationType.EMAIL);
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.PENDING);
        assertThat(notification.getRecipient().getEmail()).isEqualTo("jane@example.com");
        assertThat(notification.getRecipient().getName()).isEqualTo("Jane");
        assertThat(notification.getSubject()).isEqualTo("Transfer Successful");
    }
}
