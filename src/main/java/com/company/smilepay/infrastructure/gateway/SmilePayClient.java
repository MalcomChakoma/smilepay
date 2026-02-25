package com.company.smilepay.infrastructure.gateway;

import com.company.smilepay.infrastructure.gateway.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SmilePayClient {

    private final WebClient webClient;

    // MPGS (Visa/Mastercard, Innbucks)
    public ExpressCheckoutResponse initiate(ExpressCheckoutRequest request) {
        return webClient.post()
                .uri("/payments/express-checkout/mpgs")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ExpressCheckoutResponse.class)
                .block();
    }

    // ZB Wallet (Omari / SmileCash)
    public ExpressCheckoutResponse initiateZbPayment(ExpressCheckoutRequest request) {
        return webClient.post()
                .uri("/payments/express-checkout/zb-payment")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ExpressCheckoutResponse.class)
                .block();
    }

    public ExpressCheckoutResponse confirmZbPayment(String transactionReference, String otp) {

        Map<String, String> body = new HashMap<>();
        body.put("transactionReference", transactionReference);
        body.put("otp", otp);

        return webClient.post()
                .uri("/payments/express-checkout/zb-payment/confirmation")
                .bodyValue(body)
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