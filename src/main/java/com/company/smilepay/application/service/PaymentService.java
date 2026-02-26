package com.company.smilepay.application.service;

import com.company.smilepay.domain.model.*;
import com.company.smilepay.domain.repository.PaymentRepository;
import com.company.smilepay.application.dto.*;
import com.company.smilepay.infrastructure.gateway.SmilePayClient;
import com.company.smilepay.infrastructure.gateway.dto.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.company.smilepay.infrastructure.gateway.dto.ExpressCheckoutRequest.*;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;
    private final SmilePayClient smilePayClient;

    public PaymentResponse initiatePayment(InitiatePaymentRequest request) {

        if (repository.existsByOrderReference(request.getOrderReference())) {
            throw new RuntimeException("Duplicate order reference");
        }

        PaymentMethod method =
                PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase());

        Payment payment = Payment.builder()
                .orderReference(request.getOrderReference())
                .amount(request.getAmount())
                .currency("USD")
                .method(method)
                .status(PaymentStatus.PENDING)
                .customerPhone(request.getMobilePhoneNumber())
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(payment);

        ExpressCheckoutResponse response;

        if (method == PaymentMethod.OMARI ||
                method == PaymentMethod.SMILECASH) {

            ExpressCheckoutRequest zbRequest = builder()
                    .orderReference(request.getOrderReference())
                    .amount(payment.getAmount().doubleValue())
                    .returnUrl("https://yourdomain.com/return")
                    .resultUrl("https://yourdomain.com/api/webhook/smilepay")
                    .itemName(request.getItemName())
                    .itemDescription(request.getItemDescription())
                    .currencyCode("840")
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .mobilePhoneNumber(request.getMobilePhoneNumber())
                    .zbWalletMobile(request.getZbWalletMobile())
                    .email(request.getEmail())
                    .cancelUrl("https://yourdomain.com/cancel")
                    .failureUrl("https://yourdomain.com/failure")
                    .build();
            System.out.println("REQUEST HERE =>>>> {} " + zbRequest);

            response = smilePayClient.initiateZbPayment(zbRequest);
        }

            else if(method == PaymentMethod.ECOCASH) {
            ExpressCheckoutRequest gatewayRequest =
                    builder()
                            .orderReference(request.getOrderReference())
                            .amount(payment.getAmount().doubleValue())
                            .currencyCode("840")
                            .firstName(request.getFirstName())
                            .lastName(request.getLastName())
                            .mobilePhoneNumber(request.getMobilePhoneNumber())
                            .ecocashMobile(request.getMobilePhoneNumber())
                            .email(request.getEmail())
                            .itemName(request.getItemName())
                            .itemDescription(request.getItemDescription())
                            .returnUrl("https://yourdomain.com/return")
                            .resultUrl("https://yourdomain.com/api/webhook/smilepay")
                            .cancelUrl("https://yourdomain.com/cancel")
                            .failureUrl("https://yourdomain.com/failure")
                            .build();

            System.out.println("ECOCASH REQUEST HERE =>>>> {} " + gatewayRequest);


            response = smilePayClient.initiateEcoCash(gatewayRequest);
        }
                else if(method == PaymentMethod.INNBUCKS){
                ExpressCheckoutRequest gatewayRequest =
                        builder()
                                .orderReference(request.getOrderReference())
                                .amount(payment.getAmount().doubleValue())
                                .currencyCode("840")
                                .firstName(request.getFirstName())
                                .lastName(request.getLastName())
                                .mobilePhoneNumber(request.getMobilePhoneNumber())
                                .ecocashMobile(request.getMobilePhoneNumber())
                                .email(request.getEmail())
                                .itemName(request.getItemName())
                                .itemDescription(request.getItemDescription())
                                .returnUrl("https://yourdomain.com/return")
                                .resultUrl("https://yourdomain.com/api/webhook/smilepay")
                                .cancelUrl("https://yourdomain.com/cancel")
                                .failureUrl("https://yourdomain.com/failure")
                                .build();

                System.out.println("INNBUCKS REQUEST HERE =>>>> {} " + gatewayRequest);


                response = smilePayClient.initiateInnbucks(gatewayRequest);

            System.out.println("Gateway Response InnBucks: " + response.getInnbucksPaymentCode());
            System.out.println("Gateway Response InnBucks: " + response.getInnbucksPaymentCode());


        } else {

            ExpressCheckoutRequest gatewayRequest =
                    builder()
                            .orderReference(payment.getOrderReference())
                            .amount(payment.getAmount().doubleValue())
                            .currencyCode("840")
                            .mobilePhoneNumber(request.getMobilePhoneNumber())
                            .email(request.getEmail())
                            .firstName(request.getFirstName())
                            .lastName(request.getLastName())
                            .paymentMethod(request.getPaymentMethod())
                            .itemName(request.getItemName())
                            .itemDescription(request.getItemDescription())
                            .resultUrl("https://yourdomain.com/smilepay/api/webhook/smilepay")
                            .returnUrl("https://yourdomain.com/return")
                            .cancelUrl("https://yourdomain.com/cancel")
                            .failureUrl("https://yourdomain.com/failure")
                            .pan(request.getPan())
                            .expMonth(request.getExpMonth())
                            .expYear(request.getExpYear())
                            .securityCode(request.getSecurityCode())
                            .build();

            System.out.println("VISA/MASTERCARD REQUEST HERE =>>>> {} " + gatewayRequest);


            response = smilePayClient.initiate(gatewayRequest);
        }

        System.out.println("Gateway Response: " + response);

        if (response.getTransactionReference() != null) {
            payment.setExternalReference(response.getTransactionReference());
            repository.save(payment);
        }

        if (response.getRedirectHtml() != null &&
                !response.getRedirectHtml().isBlank()) {

            return PaymentResponse.builder()
                    .orderReference(payment.getOrderReference())
                    .status("REDIRECT_REQUIRED")
                    .redirectHtml(response.getRedirectHtml())
                    .build();
        }

        if ("OTP_REQUIRED".equalsIgnoreCase(response.getStatus())) {
            return PaymentResponse.builder()
                    .orderReference(payment.getOrderReference())
                    .status("OTP_REQUIRED")
                    .build();
        }

        PaymentResponse.PaymentResponseBuilder builder = PaymentResponse.builder()
                .orderReference(payment.getOrderReference())
                .status(response.getStatus())
                .message(response.getResponseCode())
                .transactionReference(response.getTransactionReference());

        if (method == PaymentMethod.INNBUCKS) {
            builder.innbucksPaymentCode(response.getInnbucksPaymentCode());
        }

        return builder.build();
    }

    public PaymentResponse verifyOtp(VerifyOtpRequest request) {

        Payment payment = repository.findByOrderReference(request.getOrderReference())
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.SUCCESS ||
                payment.getStatus() == PaymentStatus.FAILED) {
            return mapToResponse(payment);
        }

        if (payment.getExternalReference() == null) {
            throw new IllegalStateException("External reference missing");
        }

        ExpressCheckoutResponse response =
                smilePayClient.confirmZbPayment(
                        payment.getExternalReference(),
                        request.getOtp()
                );

        payment.setStatus(
                PaymentStatus.valueOf(response.getStatus().trim().toUpperCase())
        );

        payment.setExternalReference(response.getTransactionReference());

        repository.save(payment);

        return mapToResponse(payment);
    }
    public PaymentResponse checkPaymentStatus(String orderReference) {

        Payment payment = repository.findByOrderReference(orderReference)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        ExpressCheckoutResponse response =
                smilePayClient.checkStatus(orderReference);

        payment.setStatus(PaymentStatus.valueOf(response.getStatus()));

        repository.save(payment);

        return mapToResponse(payment);
    }

    public void updateStatus(String orderReference, PaymentStatus newStatus) {

        Payment payment = repository.findByOrderReference(orderReference)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.PAID ||
                payment.getStatus() == PaymentStatus.FAILED) {
            return;
        }

        payment.setStatus(newStatus);
        repository.save(payment);
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .orderReference(payment.getOrderReference())
                .status(payment.getStatus().name())
                .message("Current status: " + payment.getStatus().name())
                .transactionReference(payment.getExternalReference())
                .build();
    }
}