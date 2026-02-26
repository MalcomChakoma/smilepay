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

    public ExpressCheckoutResponse confirmZbPayment(String externalReference, String otp) {

        Map<String, String> body = new HashMap<>();
        body.put("transactionReference", externalReference);
        body.put("otp", otp);

        System.out.println("Sending OTP confirmation request for transactionReference: "
                + externalReference);

        ExpressCheckoutResponse response = webClient.post()
                .uri("/payments/express-checkout/zb-payment/confirmation")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(ExpressCheckoutResponse.class)
                .block();

        System.out.println("OTP confirmation response: " + response);

        return response;
    }
    public ExpressCheckoutResponse checkStatus(String orderReference) {
        return webClient.get()
                .uri("/payments/transaction/{ref}/status/check", orderReference)
                .retrieve()
                .bodyToMono(ExpressCheckoutResponse.class)
                .block();
    }

    public ExpressCheckoutResponse initiateEcoCash(ExpressCheckoutRequest request) {

        return webClient.post()
                .uri("/payments/express-checkout/ecocash")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ExpressCheckoutResponse.class)
                .block();
    }

    public ExpressCheckoutResponse initiateInnbucks(ExpressCheckoutRequest request) {

        return webClient.post()
                .uri("/payments/express-checkout/innbucks")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ExpressCheckoutResponse.class)
                .block();
    }

}