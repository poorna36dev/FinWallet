package com.poorna.fintech.notification;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import com.poorna.fintech.entity.Notification;

@Component
@RequiredArgsConstructor
public class EmailVerificationListener {
    
    private final EmailService emailService;
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async 
    public void handleEmailverification(Notification notification){
        emailService.sendEmail(notification);
    }
}
