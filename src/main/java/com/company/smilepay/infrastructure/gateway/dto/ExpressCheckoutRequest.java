package com.company.smilepay.infrastructure.gateway.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class ExpressCheckoutRequest {

    private String orderReference;
    private Double amount;
    private String currencyCode;

    private String firstName;
    private String lastName;
    private String mobilePhoneNumber;
    private String zbWalletMobile;
    private String ecocashMobile;
    private String email;

    private String resultUrl;
    private String returnUrl;
    private String cancelUrl;
    private String failureUrl;

    private String itemName;
    private String itemDescription;

    private String paymentMethod;

    private String pan;
    private String expMonth;
    private String expYear;
    private String securityCode;
}