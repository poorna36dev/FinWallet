package com.poorna.fintech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.poorna.fintech.entity.Notification;
import com.poorna.fintech.entity.NotificationStatus;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificatioRepo extends JpaRepository<Notification, Long>{
    @Query("""
            SELECT n
            FROM Notification n
            WHERE n.status = :status
            AND n.retryCount < :maxRetries
            AND n.nextRetryAt <= :time
            """)
    Page<Notification> findNotifications(NotificationStatus status,int maxRetries,LocalDateTime time,Pageable pageble);
}
