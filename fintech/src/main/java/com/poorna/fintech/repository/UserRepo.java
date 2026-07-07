package com.poorna.fintech.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.poorna.fintech.entity.User;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findById(long userId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.wallets w LEFT JOIN FETCH w.transactions WHERE u.id = :userId")
    Optional<User> findByIdJoinFetch(@Param("userId") long userId);

    Optional<User> findByUserName(String username);

    boolean existsByUserNameAndEmail(String username, String email);

    boolean existsByEmail( String email);

    Optional<User> findByEmail(String email);
}
