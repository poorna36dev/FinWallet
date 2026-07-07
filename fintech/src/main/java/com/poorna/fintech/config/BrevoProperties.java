package com.poorna.fintech.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "brevo")
public class BrevoProperties {
    
    private String apiKey;
    private String baseUrl;
    private String senderEmail;
    private String senderName;

}