package com.poorna.fintech.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.poorna.fintech.dtos.PageResponse;
import com.poorna.fintech.dtos.SysDeposit;
import com.poorna.fintech.dtos.TransactionResponse;
import com.poorna.fintech.dtos.TransferRequest;
import com.poorna.fintech.dtos.WalletRequest;
import com.poorna.fintech.dtos.WalletResponse;
import com.poorna.fintech.dtos.WithdrawRequest;
import com.poorna.fintech.entity.Wallet;
import com.poorna.fintech.exception.InsufficientBalanceException;
import com.poorna.fintech.service.WalletService;
import com.poorna.fintech.service.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import com.poorna.fintech.dtos.ErrorResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
@Tag(name = "Wallet Management", description = "Endpoints for creating wallets, transferring funds, and reviewing transaction history")
public class WalletController {
    private final WalletService walletService;

    @Operation(
        summary = "Create a new wallet",
        description = "Creates a wallet for the authenticated user in the specified currency."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Wallet created successfully"),
        @ApiResponse(responseCode = "400", description = "The supplied wallet creation payload is invalid", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/create")
    public ResponseEntity<Wallet> createWallet(@Valid @RequestBody WalletRequest request,Authentication principal) {
        UserDetailsImpl user = (UserDetailsImpl) principal.getPrincipal();
        Wallet wallet = walletService.createWallet(request.getName(), request.getCurrency(),user);
        return ResponseEntity.created(null).body(wallet);
    }
    @Operation(
        summary = "Deposit funds into a wallet",
        description = "Adds funds to a specified wallet. This operation is restricted to administrators."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Deposit completed successfully"),
        @ApiResponse(responseCode = "404", description = "The target wallet does not exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/sysdeposit")
    public ResponseEntity<WalletResponse> sysDeposit(@Valid @RequestBody SysDeposit request,Authentication principal) {
        UserDetailsImpl user=(UserDetailsImpl)principal.getPrincipal();
        WalletResponse wallet = walletService.depositAmount(request.getWalletId(), request.getAmount(),user);
        return ResponseEntity.ok(wallet);
    }
    @Operation(
        summary = "Transfer funds between wallets",
        description = "Transfers funds from the authenticated user's wallet to another wallet."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transfer completed successfully"),
        @ApiResponse(responseCode = "400", description = "The transfer request is invalid or the balance is insufficient", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transferAmount(@Valid @RequestBody TransferRequest request,Authentication principal) throws InsufficientBalanceException,InterruptedException {
        UserDetailsImpl user=(UserDetailsImpl)principal.getPrincipal();
        TransactionResponse wallet = walletService.TransferAmount(user,request);
        return ResponseEntity.ok(wallet);
    }
    @Operation(
        summary = "Withdraw funds from a wallet",
        description = "Withdraws funds from a specified wallet. This operation is restricted to administrators."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Withdrawal completed successfully"),
        @ApiResponse(responseCode = "400", description = "The withdrawal request is invalid or the balance is insufficient", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/withdraw")
    public ResponseEntity<WalletResponse> withdrawAmount(@Valid @RequestBody WithdrawRequest request,Authentication principal) throws InsufficientBalanceException {
        UserDetailsImpl user=(UserDetailsImpl)principal.getPrincipal();
        WalletResponse wallet = walletService.withdrawAmount(request.getWalletId(), request.getAmount(), user);
        return ResponseEntity.ok(wallet);
    }

    @Operation(
        summary = "Retrieve wallet transactions",
        description = "Returns a paginated list of transactions for the specified wallet."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transaction list retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "The wallet for the requested transaction history does not exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/transactions/{walletId}")
    public ResponseEntity<PageResponse<TransactionResponse>> getAllTransactions(
        @Validated @PathVariable long walletId,
        Authentication principal,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) 
    {
        UserDetailsImpl user=(UserDetailsImpl)principal.getPrincipal();
        PageResponse<TransactionResponse> transactions = walletService.getAllTransactions(walletId, user,page,size);
        return ResponseEntity.ok(transactions);
    }
    
    @Operation(
        summary = "Get wallet details",
        description = "Returns the details of the specified wallet for the authenticated user."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Wallet details retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "The requested wallet does not exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/getWallets/{id}")
    public ResponseEntity<WalletResponse> getWalletById(@Validated @PathVariable Long id,Authentication principal) {
        UserDetailsImpl user=(UserDetailsImpl)principal.getPrincipal();
        return ResponseEntity.ok(walletService.getWalletsById(id, user));
    }

}
