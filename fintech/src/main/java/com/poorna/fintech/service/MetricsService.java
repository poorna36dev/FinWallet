package com.poorna.fintech.service;

import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Service
public class MetricsService {

    private final Counter transferSuccessCounter;
    private final Counter transferFailureCounter;
    private final Counter emailSentCounter;
    private final Counter emailFailureCounter;

    public MetricsService(MeterRegistry meterRegistry) {

        transferSuccessCounter = Counter.builder("fintech.transfer.success")
                .description("Number of successful transfers")
                .register(meterRegistry);

        transferFailureCounter = Counter.builder("fintech.transfer.failure")
                .description("Number of failed transfers")
                .register(meterRegistry);

        emailSentCounter = Counter.builder("fintech.email.sent")
                .description("Number of emails sent")
                .register(meterRegistry);

        emailFailureCounter = Counter.builder("fintech.email.failure")
                .description("Number of failed emails")
                .register(meterRegistry);
    }

    public void transferSuccess() {
        transferSuccessCounter.increment();
    }

    public void transferFailure() {
        transferFailureCounter.increment();
    }

    public void emailSent() {
        emailSentCounter.increment();
    }

    public void emailFailure() {
        emailFailureCounter.increment();
    }
}
