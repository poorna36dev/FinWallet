package com.poorna.fintech.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.poorna.fintech.dtos.TransactionResponse;
import com.poorna.fintech.dtos.TransferRequest;
import com.poorna.fintech.dtos.WalletResponse;
import com.poorna.fintech.service.UserDetailsImpl;
import com.poorna.fintech.service.WalletService;
import com.poorna.fintech.entity.User;

@ExtendWith(MockitoExtension.class)
class WalletControllerTest {

    @Mock
    private WalletService walletService;

    @InjectMocks
    private WalletController walletController;

    @Test
    void getWalletByIdReturnsWalletResponseForAuthenticatedUser() {
        User user = new User();
        user.setId(1L);
        user.setUserName("jdoe");
        user.setName("Jane");
        user.setEmail("jane@example.com");

        WalletResponse response = new WalletResponse();
        response.setId(10L);
        response.setName("Primary");

        Authentication authentication = org.mockito.Mockito.mock(Authentication.class);
        org.mockito.Mockito.when(authentication.getPrincipal()).thenReturn(new UserDetailsImpl(user));

        when(walletService.getWalletsById(eq(10L), any(UserDetailsImpl.class))).thenReturn(response);

        ResponseEntity<WalletResponse> result = walletController.getWalletById(10L, authentication);

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getId()).isEqualTo(10L);
    }

    @Test
    void transferAmountReturnsTransactionResponseForAuthenticatedUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUserName("jdoe");
        user.setName("Jane");
        user.setEmail("jane@example.com");

        TransferRequest request = new TransferRequest();
        request.setSourceWalletId(1L);
        request.setDestinationWalletId(2L);
        request.setAmount(java.math.BigDecimal.TEN);

        TransactionResponse response = new TransactionResponse();
        response.setId(99L);
        response.setStatus("SUCCESS");

        Authentication authentication = org.mockito.Mockito.mock(Authentication.class);
        org.mockito.Mockito.when(authentication.getPrincipal()).thenReturn(new UserDetailsImpl(user));

        when(walletService.TransferAmount(any(UserDetailsImpl.class), any(TransferRequest.class))).thenReturn(response);

        ResponseEntity<TransactionResponse> result = walletController.transferAmount(request, authentication);

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getId()).isEqualTo(99L);
    }
}
