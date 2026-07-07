package com.poorna.fintech.service;

import com.poorna.fintech.entity.User;
import com.poorna.fintech.entity.VerificationPurpose;

public interface EmailVerificationService {

    void sendVerificationEmail(User user,VerificationPurpose purpose,String targetEmail);

    void verifyEmail(String token);

}