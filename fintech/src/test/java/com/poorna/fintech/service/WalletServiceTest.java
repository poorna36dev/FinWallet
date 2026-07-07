package com.poorna.fintech.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.poorna.fintech.Aop.BusinessLogger;
import com.poorna.fintech.dtos.TransactionResponse;
import com.poorna.fintech.dtos.TransferCompletedEvent;
import com.poorna.fintech.dtos.TransferRequest;
import com.poorna.fintech.entity.Transaction;
import com.poorna.fintech.entity.User;
import com.poorna.fintech.entity.Wallet;
import com.poorna.fintech.exception.InsufficientBalanceException;
import com.poorna.fintech.exception.WalletNotFoundException;
import com.poorna.fintech.repository.TransactionRepo;
import com.poorna.fintech.repository.WalletRepo;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private MetricsService metricsService;

    @Mock
    private WalletRepo walletRepo;

    @Mock
    private TransactionRepo transactionRepo;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private BusinessLogger businessLogger;

    @InjectMocks
    private WalletService walletService;

    @Test
    void depositAmountUpdatesBalanceAndSavesTransaction() {
        User user = new User();
        user.setUserName("jdoe");
        user.setName("Jane");
        user.setEmail("jane@example.com");

        UserDetailsImpl principal = new UserDetailsImpl(user);
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(BigDecimal.TEN);
        wallet.setCurrency("USD");
        wallet.setTransactions(new java.util.ArrayList<>());

        when(walletRepo.findByIdAndUserUsername(1L, "jdoe")).thenReturn(Optional.of(wallet));
        when(transactionRepo.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = walletService.depositAmount(1L, BigDecimal.ONE, principal);

        assertThat(response.getBalance()).isEqualByComparingTo("11");
        assertThat(wallet.getTransactions()).hasSize(1);
        verify(transactionRepo).save(any(Transaction.class));
    }

    @Test
    void transferAmountThrowsWhenSourceWalletHasInsufficientBalance() {
        User user = new User();
        user.setUserName("jdoe");
        user.setName("Jane");
        user.setEmail("jane@example.com");

        UserDetailsImpl principal = new UserDetailsImpl(user);
        Wallet sourceWallet = new Wallet();
        sourceWallet.setId(1L);
        sourceWallet.setBalance(BigDecimal.ONE);
        sourceWallet.setCurrency("USD");
        sourceWallet.setTransactions(new java.util.ArrayList<>());

        Wallet destinationWallet = new Wallet();
        destinationWallet.setId(2L);
        destinationWallet.setBalance(BigDecimal.ZERO);
        destinationWallet.setCurrency("USD");

        TransferRequest request = new TransferRequest();
        request.setSourceWalletId(1L);
        request.setDestinationWalletId(2L);
        request.setAmount(BigDecimal.TEN);

        when(walletRepo.findByIdAndUserUsername(1L, "jdoe")).thenReturn(Optional.of(sourceWallet));
        when(walletRepo.findById(2L)).thenReturn(Optional.of(destinationWallet));

        assertThatThrownBy(() -> walletService.TransferAmount(principal, request))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessage("Insufficient balance.");
    }

    @Test
    void transferAmountThrowsWhenDestinationWalletDoesNotExist() {
        User user = new User();
        user.setUserName("jdoe");
        user.setName("Jane");
        user.setEmail("jane@example.com");

        UserDetailsImpl principal = new UserDetailsImpl(user);
        Wallet sourceWallet = new Wallet();
        sourceWallet.setId(1L);
        sourceWallet.setBalance(BigDecimal.TEN);
        sourceWallet.setCurrency("USD");
        sourceWallet.setTransactions(new java.util.ArrayList<>());

        TransferRequest request = new TransferRequest();
        request.setSourceWalletId(1L);
        request.setDestinationWalletId(99L);
        request.setAmount(BigDecimal.ONE);

        when(walletRepo.findByIdAndUserUsername(1L, "jdoe")).thenReturn(Optional.of(sourceWallet));
        when(walletRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> walletService.TransferAmount(principal, request))
                .isInstanceOf(WalletNotFoundException.class)
                .hasMessage("Destination wallet not found.");
    }

    @Test
    void transferAmountCompletesSuccessfullyAndPersistsTransaction() throws Exception {
        User user = new User();
        user.setUserName("jdoe");
        user.setName("Jane");
        user.setEmail("jane@example.com");

        UserDetailsImpl principal = new UserDetailsImpl(user);
        Wallet sourceWallet = new Wallet();
        sourceWallet.setId(1L);
        sourceWallet.setBalance(BigDecimal.TEN);
        sourceWallet.setCurrency("USD");
        sourceWallet.setTransactions(new java.util.ArrayList<>());

        Wallet destinationWallet = new Wallet();
        destinationWallet.setId(2L);
        destinationWallet.setBalance(BigDecimal.ZERO);
        destinationWallet.setCurrency("USD");

        Transaction transaction = new Transaction();
        transaction.setId(55L);
        transaction.setAmount(BigDecimal.ONE);
        transaction.setCurrency("USD");
        transaction.setType("Transfer");
        transaction.setStatus("SUCCESS");

        TransferRequest request = new TransferRequest();
        request.setSourceWalletId(1L);
        request.setDestinationWalletId(2L);
        request.setAmount(BigDecimal.ONE);

        when(walletRepo.findByIdAndUserUsername(1L, "jdoe")).thenReturn(Optional.of(sourceWallet));
        when(walletRepo.findById(2L)).thenReturn(Optional.of(destinationWallet));
        when(transactionRepo.save(any(Transaction.class))).thenReturn(transaction);

        TransactionResponse response = walletService.TransferAmount(principal, request);

        assertThat(response.getId()).isEqualTo(55L);
        assertThat(sourceWallet.getBalance()).isEqualByComparingTo("9");
        assertThat(destinationWallet.getBalance()).isEqualByComparingTo("1");
        verify(metricsService).transferSuccess();
        verify(eventPublisher).publishEvent(any(TransferCompletedEvent.class));
    }
}
