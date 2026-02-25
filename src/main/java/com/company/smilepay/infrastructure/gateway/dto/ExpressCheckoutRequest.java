package com.company.smilepay.infrastructure.gateway.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExpressCheckoutRequest {

    private String orderReference;
    private Double amount;
    private String returnUrl;
    private String resultUrl;
    private String itemName;
    private String itemDescription;
    private String currencyCode;
    private String firstName;
    private String lastName;
    private String mobilePhoneNumber;
    private String email;
    private String paymentMethod;
    private String cancelUrl;
    private String failureUrl;

    // Card only
    private String pan;
    private String expMonth;
    private String expYear;
    private String securityCode;
}