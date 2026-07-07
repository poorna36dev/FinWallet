package com.poorna.fintech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.poorna.fintech.entity.PasswordResetToken;

public interface PasswordResetRepo extends JpaRepository<PasswordResetToken,Long>{
 
    void deleteByUserId(Long userId);

    Optional<PasswordResetToken> findByToken(String token);
    
    Optional<PasswordResetToken> findByUserId(Long userId);
}
