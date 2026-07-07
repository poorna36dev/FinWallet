package com.poorna.fintech.notification.dto;

import lombok.Data;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor  
@AllArgsConstructor
public class SendEmailRequest {
    
    private Sender sender;
    @JsonProperty("to")
    private List<Recipient> recipients;
    private String subject;
    private String htmlContent;
}
