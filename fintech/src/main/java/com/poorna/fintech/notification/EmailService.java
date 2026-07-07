package com.poorna.fintech.notification;

import com.poorna.fintech.entity.Notification;

public interface EmailService {
    
    void sendEmail(Notification notification) ;

}
