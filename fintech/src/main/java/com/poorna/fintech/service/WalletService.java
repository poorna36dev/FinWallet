package com.poorna.fintech.service;

import java.math.BigDecimal;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import com.poorna.fintech.dtos.PageResponse;
import com.poorna.fintech.dtos.TransactionResponse;
import com.poorna.fintech.dtos.TransferCompletedEvent;
import com.poorna.fintech.dtos.TransferRequest;
import com.poorna.fintech.dtos.WalletResponse;
import com.poorna.fintech.entity.Transaction;
import com.poorna.fintech.entity.Wallet;
import com.poorna.fintech.exception.InsufficientBalanceException;
import com.poorna.fintech.exception.WalletNotFoundException;
import com.poorna.fintech.repository.TransactionRepo;
import com.poorna.fintech.repository.WalletRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.poorna.fintech.Aop.Idempotency;
import com.poorna.fintech.Aop.BusinessLogger;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final MetricsService metricsService;
    private final WalletRepo walletRepo;
    private final TransactionRepo transactionrepo;
    private final ApplicationEventPublisher eventPublisher;
    private final BusinessLogger businessLogger;

    public Wallet createWallet(String name, String currency, UserDetailsImpl user) {
        Wallet wallet = new Wallet();
        wallet.setName(name);
        wallet.setCurrency(currency);
        wallet.setBalance(java.math.BigDecimal.ZERO);
        wallet.setUser(user.getUser());
        return walletRepo.save(wallet);
    }

    @Transactional
    @CachePut(value = "wallets", key = "#id")
    public WalletResponse depositAmount(long id, BigDecimal amount, UserDetailsImpl user) {
        String userName = user.getUsername();
        Wallet wallet = walletRepo.findByIdAndUserUsername(id, userName)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));
        wallet.setBalance(wallet.getBalance().add(amount));
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDestinationWallet(wallet);
        transaction.setType("DEPOSIT");
        transaction.setStatus("SUCCESS");
        transaction.setCurrency(wallet.getCurrency());
        transaction.setSourceWallet(null); // Assuming no source wallet for system deposits
        wallet.getTransactions().add(transaction);
        transactionrepo.save(transaction);
        return toResponse(wallet);

    }
    @Idempotency
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "wallets", key = "#request.sourceWalletId"),
            @CacheEvict(value = "wallets", key = "#request.destinationWalletId")
    })
    public TransactionResponse TransferAmount (
            UserDetailsImpl user,
            TransferRequest request) throws InsufficientBalanceException {
        Transaction transaction=null;
        try{
        Wallet sourceWallet = walletRepo
                .findByIdAndUserUsername(
                        request.getSourceWalletId(),
                        user.getUsername())
                .orElseThrow(() ->
                        new WalletNotFoundException("Wallet not found."));

        Wallet destinationWallet = walletRepo
                .findById(request.getDestinationWalletId())
                .orElseThrow(() ->
                        new WalletNotFoundException("Destination wallet not found."));

        if (sourceWallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance.");
        }

        sourceWallet.setBalance(
                sourceWallet.getBalance().subtract(request.getAmount()));

        destinationWallet.setBalance(
                destinationWallet.getBalance().add(request.getAmount()));

        transaction = new Transaction();

        transaction.setAmount(request.getAmount());
        transaction.setCurrency(sourceWallet.getCurrency());
        transaction.setType("Transfer");
        transaction.setStatus("SUCCESS");
        transaction.setSourceWallet(sourceWallet);
        transaction.setDestinationWallet(destinationWallet);
        sourceWallet.getTransactions().add(transaction);
        transaction =transactionrepo.save(transaction);
        TransferCompletedEvent event = new TransferCompletedEvent(
                transaction.getId(),
                sourceWallet.getId(),
                destinationWallet.getId(),
                user.getUsername(),
                user.getUser().getName(),
                user.getUser().getEmail(),
                request.getAmount(),
                sourceWallet.getCurrency(),
                transaction.getCreatedAt()
        );
        eventPublisher.publishEvent(event);
        businessLogger.transferCompleted(transaction.getId(),user.getUsername(),sourceWallet.getId(),destinationWallet.getId(),transaction.getAmount(),transaction.getCurrency());
        metricsService.transferSuccess();
        }
        catch(InsufficientBalanceException e){
                metricsService.transferFailure();
                throw e;
        } 
        return buildTransferResponse(transaction);
              
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
    @Transactional
    @CacheEvict(value = "wallets", key = "#id")
    public WalletResponse withdrawAmount(long id, BigDecimal amount, UserDetailsImpl user) throws InsufficientBalanceException {
        Wallet wallet = walletRepo.findByIdAndUserUsername(id, user.getUsername())
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setSourceWallet(wallet);
        transaction.setType("WITHDRAWAL");
        transaction.setStatus("SUCCESS");
        transaction.setCurrency(wallet.getCurrency());
        transaction.setDestinationWallet(null); // Assuming no destination wallet for withdrawals
        return toResponse(wallet);
    }

    @Cacheable(
    value = "transactions",
    key = "#walletId + '-' + #page + '-' + #size"
)
public PageResponse<TransactionResponse> getAllTransactions(
        long walletId,
        UserDetailsImpl user,
        int page,
        int size) {

    Wallet wallet = walletRepo
            .findByIdAndUserUsername(walletId, user.getUsername())
            .orElseThrow(() ->
                    new WalletNotFoundException("Wallet not found."));

    Page<Transaction> transactions = transactionrepo.findBySourceWallet(
            wallet,
            PageRequest.of(
                    page,
                    size,
                    Sort.by("createdAt").descending()));

    return toPageResponse(transactions);
}

    @Cacheable(cacheNames = "wallets", key = "#id", condition = "#id > 20")
    public WalletResponse getWalletsById(Long id, UserDetailsImpl user) {
        return toResponse(walletRepo.findByIdAndUserUsername(id, user.getUsername())
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found")));
    }

    private WalletResponse toResponse(Wallet wallet) {
        WalletResponse response = new WalletResponse();
        response.setId(wallet.getId());
        response.setBalance(wallet.getBalance());
        response.setName(wallet.getName());
        response.setCurrency(wallet.getCurrency());
        return response;
    }

    private TransactionResponse toTransactionResponse(Transaction transaction) {

    TransactionResponse response = new TransactionResponse();

    response.setId(transaction.getId());
    response.setAmount(transaction.getAmount());
    response.setCurrency(transaction.getCurrency());
    response.setStatus(transaction.getStatus());
    response.setType(transaction.getType());

    return response;
}
private PageResponse<TransactionResponse> toPageResponse(
        Page<Transaction> page) {

    PageResponse<TransactionResponse> response =
            new PageResponse<>();

    response.setContent(
            page.getContent()
                    .stream()
                    .map(this::toTransactionResponse)
                    .toList());

    response.setPage(page.getNumber());
    response.setSize(page.getSize());
    response.setTotalPages(page.getTotalPages());
    response.setTotalElements(page.getTotalElements());
    response.setFirst(page.isFirst());
    response.setLast(page.isLast());
    response.setHasNext(page.hasNext());
    response.setHasPrevious(page.hasPrevious());

    return response;
}
}
