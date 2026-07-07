package com.poorna.fintech.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.poorna.fintech.entity.Transaction;
import com.poorna.fintech.entity.Wallet;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {

    @Query("select t from Transaction t where t.sourceWallet=:sourceWallet ")
    List<Transaction> findBySourceWallet(@Param("sourceWallet") Wallet wallet);

    Page<Transaction> findBySourceWallet(
        Wallet wallet,
        Pageable pageable);
}
