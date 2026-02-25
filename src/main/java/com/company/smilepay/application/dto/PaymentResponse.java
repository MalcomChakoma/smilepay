package com.company.smilepay.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {

    private String orderReference;
    private String status;
    private String paymentCode;
    private String redirectHtml;
    private String message;

    public PaymentResponse(String orderReference, String name) {
    }
}