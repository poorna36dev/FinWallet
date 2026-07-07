package com.poorna.fintech.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data; 
import lombok.NoArgsConstructor;
import jakarta.persistence.Embeddable;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Recipient {

    private String email;

    private String name;

}