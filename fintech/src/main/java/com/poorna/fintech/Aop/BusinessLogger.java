package com.poorna.fintech.Aop;

import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BusinessLogger {

    private static final Logger log = LoggerFactory.getLogger(BusinessLogger.class);

    public void transferCompleted(
            Long transactionId,
            String username,
            Long sourceWalletId,
            Long destinationWalletId,
            BigDecimal amount,
            String currency) {

        log.info(
                "BUSINESS_EVENT=TRANSFER_COMPLETED transactionId={} username={} sourceWallet={} destinationWallet={} amount={} currency={}",
                transactionId,
                username,
                sourceWalletId,
                destinationWalletId,
                amount,
                currency);
    }

    public void emailSent(Long notificationId, String recipient) {

        log.info(
                "BUSINESS_EVENT=EMAIL_SENT notificationId={} recipient={}",
                notificationId,
                recipient);
    }

    public void emailFailed(Long notificationId, String recipient, int retryCount) {

        log.warn(
                "BUSINESS_EVENT=EMAIL_FAILED notificationId={} recipient={} retryCount={}",
                notificationId,
                recipient,
                retryCount);
    }

    public void notificationRetry(Long notificationId, int retryCount) {

        log.info(
                "BUSINESS_EVENT=NOTIFICATION_RETRY notificationId={} retryCount={}",
                notificationId,
                retryCount);
    }
}
