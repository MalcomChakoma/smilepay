package com.company.smilepay.infrastructure.gateway;

import com.company.smilepay.infrastructure.gateway.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class SmilePayClient {

    private final WebClient webClient;

    public ExpressCheckoutResponse initiate(ExpressCheckoutRequest request) {

        return webClient.post()
                .uri("/payments/express-checkout/mpgs")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ExpressCheckoutResponse.class)
                .block();
    }

    public ExpressCheckoutResponse confirmOtp(String orderReference, String otp) {

        return webClient.post()
                .uri("/payments/express-checkout/confirm")
                .bodyValue(
                        java.util.Map.of(
                                "orderReference", orderReference,
                                "otp", otp
                        )
                )
                .retrieve()
                .bodyToMono(ExpressCheckoutResponse.class)
                .block();
    }

    public ExpressCheckoutResponse initiateZbPayment(ExpressCheckoutRequest request) {

        return webClient.post()
                .uri("/payments/express-checkout/zb-payment")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ExpressCheckoutResponse.class)
                .block();
    }

    public ExpressCheckoutResponse confirmZbPayment(String transactionReference, String otp) {

        return webClient.post()
                .uri("/payments/express-checkout/zb-payment/confirmation")
                .bodyValue(
                        java.util.Map.of(
                                "otp", otp,
                                "transactionReference", transactionReference
                        )
                )
                .retrieve()
                .bodyToMono(ExpressCheckoutResponse.class)
                .block();
    }

    public ExpressCheckoutResponse checkStatus(String orderReference) {

        return webClient.get()
                .uri("/payments/transaction/{ref}/status/check", orderReference)
                .retrieve()
                .bodyToMono(ExpressCheckoutResponse.class)
                .block();
    }
}