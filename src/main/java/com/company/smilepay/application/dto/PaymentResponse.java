package com.company.smilepay.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse {

    private String orderReference;
    private String status;
    private String responseCode;
    private String redirectHtml;
    private String message;
    private String transactionReference;
    private String innbucksPaymentCode;

    public PaymentResponse(String orderReference, String name) {
    }
}