package com.company.smilepay.application.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class InitiatePaymentRequest {

    private String orderReference;
    private BigDecimal amount;
    private String phone;
    private String email;
    private String paymentMethod;
    private String pan;
    private String expMonth;
    private String expYear;
    private String securityCode;
}