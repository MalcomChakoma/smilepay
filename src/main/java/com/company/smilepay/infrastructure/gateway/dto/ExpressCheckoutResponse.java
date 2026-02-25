package com.company.smilepay.infrastructure.gateway.dto;

import lombok.Data;

@Data
public class ExpressCheckoutResponse {

    private String responseMessage;
    private String responseCode;
    private String status;
    private String transactionReference;

    private String gatewayRecommendation;
    private String authenticationStatus;
    private String redirectHtml;
}