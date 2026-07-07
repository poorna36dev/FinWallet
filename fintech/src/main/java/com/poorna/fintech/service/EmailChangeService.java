package com.poorna.fintech.service;

import com.poorna.fintech.dtos.ChangeEmailRequest;

public interface EmailChangeService {

    void requestEmailChange(long userId, ChangeEmailRequest request);

}