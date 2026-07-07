package com.poorna.fintech.Aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.poorna.fintech.dtos.TransactionResponse;
import com.poorna.fintech.dtos.TransferRequest;
import com.poorna.fintech.entity.Idempotent;
import com.poorna.fintech.entity.Status;
import com.poorna.fintech.entity.Transaction;
import com.poorna.fintech.exception.BadRequestException;
import com.poorna.fintech.exception.RequestAlreadyProcessingException;
import com.poorna.fintech.repository.IdempotentRepo;
import com.poorna.fintech.repository.TransactionRepo;
import com.poorna.fintech.service.HashService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class IdempotentAspect {

    private final HashService hashService;
    private final TransactionRepo transactionrepo;
    private final IdempotentRepo idempotentRepo;
    
    @Around("@annotation(com.poorna.fintech.Aop.Idempotency)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Idempotency Aspect Triggered");

        ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        HttpServletRequest request = attributes.getRequest();

        String key = request.getHeader("Idempotency-Key");
        if (key == null || key.isBlank()) {
            throw new BadRequestException(
                "Missing Idempotency-Key header");
        }
        TransferRequest transferRequest = null;

        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof TransferRequest transferRequestArg) {
                transferRequest = transferRequestArg;
                break;
            }
        }
        String requestHash = hashService.generateHash(transferRequest);
        Idempotent record = idempotentRepo
                .findByIdempotentKey(key)
                .orElse(null);

        if (record != null) {

            if (!record.getRequestHash().equals(requestHash)) {
                throw new BadRequestException(
                        "Idempotency key reused with a different request.");
            }

            switch (record.getStatus()) {

                case SUCCESS:

                    Transaction previousTransaction =
                            transactionrepo.findById(record.getTransactionId())
                                    .orElseThrow(() ->
                                            new IllegalStateException(
                                                    "Transaction missing for successful idempotency record."));

                    return buildTransferResponse(previousTransaction);

                case PENDING:

                    throw new RequestAlreadyProcessingException(
                            "Request is already being processed.");
            }
        }

        record = new Idempotent();

        record.setIdempotentKey(key);
        record.setRequestHash(requestHash);
        record.setStatus(Status.PENDING);

        try {

            record = idempotentRepo.save(record);

        } catch (DuplicateKeyException ex) {

            Idempotent existing = idempotentRepo
                    .findByIdempotentKey(key)
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "Duplicate key exists but record not found."));

            if (!existing.getRequestHash().equals(requestHash)) {
                throw new BadRequestException(
                        "Idempotency key reused with a different request.");
            }

            if (existing.getStatus() == Status.SUCCESS) {

                Transaction previousTransaction =
                        transactionrepo.findById(existing.getTransactionId())
                                .orElseThrow(() ->
                                        new IllegalStateException(
                                                "Transaction missing for successful idempotency record."));

                return buildTransferResponse(previousTransaction);
            }

            throw new RequestAlreadyProcessingException(
                    "Request is already being processed.");
        }
        Object result = joinPoint.proceed();

        TransactionResponse response = (TransactionResponse) result;

        record.setStatus(Status.SUCCESS);
        record.setTransactionId(response.getId());

        idempotentRepo.save(record);

        return result;
    }

    private TransactionResponse buildTransferResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setAmount(transaction.getAmount());
        response.setType(transaction.getType());
        response.setStatus(transaction.getStatus());
        response.setCurrency(transaction.getCurrency());
        return response;
    }
}
