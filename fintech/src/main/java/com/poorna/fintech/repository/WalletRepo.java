package com.poorna.fintech.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.poorna.fintech.entity.Wallet;

public interface WalletRepo extends JpaRepository<Wallet, Long> {
    @Query("select w from Wallet w where w.id=:id")
    Optional<Wallet> findById(long id);

    @Query("select w from Wallet w where w.id=:id and w.user.userName=:userName")
    Optional<Wallet> findByIdAndUserUsername(Long id, String userName);
}
