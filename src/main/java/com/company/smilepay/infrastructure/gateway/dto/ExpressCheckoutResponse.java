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
    private CustomizedHtml customizedHtml;

    @Data
    public static class CustomizedHtml {
        private ThreeDs2 threeDs2;
    }

    @Data
    public static class ThreeDs2 {
        private String acsUrl;
        private String cReq;
    }
}