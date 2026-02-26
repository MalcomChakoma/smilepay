package com.company.smilepay.application.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data

public class InitiatePaymentRequest {

    private String orderReference;
    private BigDecimal amount;
    private String currencyCode;
    private String paymentMethod;

    private String firstName;
    private String lastName;
    private String MobilePhoneNumber;
    private String zbWalletMobile;
    private String email;

    private String itemName;
    private String itemDescription;

    private String returnUrl;
    private String resultUrl;
    private String cancelUrl;
    private String failureUrl;

    private String pan;
    private String expMonth;
    private String expYear;
    private String securityCode;
}