package com.company.smilepay.application.service;

import com.company.smilepay.domain.model.*;
import com.company.smilepay.domain.repository.PaymentRepository;
import com.company.smilepay.application.dto.*;
import com.company.smilepay.infrastructure.gateway.SmilePayClient;
import com.company.smilepay.infrastructure.gateway.dto.ExpressCheckoutRequest;

import com.company.smilepay.infrastructure.gateway.dto.ExpressCheckoutResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;
    private final SmilePayClient smilePayClient;

    public PaymentResponse initiateEcoCash(InitiatePaymentRequest request) {

        if (repository.existsByOrderReference(request.getOrderReference())) {
            throw new RuntimeException("Duplicate order reference");
        }

        Payment payment = Payment.builder()
                .orderReference(request.getOrderReference())
                .amount(request.getAmount())
                .currency("USD")
                .method(PaymentMethod.ECOCASH)
                .status(PaymentStatus.PENDING)
                .customerPhone(request.getPhone())
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(payment);

        return new PaymentResponse(
                payment.getOrderReference(),
                payment.getStatus().name()
        );
    }

    public PaymentResponse getStatus(String orderReference) {

        Payment payment = repository.findByOrderReference(orderReference)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        return new PaymentResponse(
                payment.getOrderReference(),
                payment.getStatus().name()
        );
    }

    public void updateStatus(String orderReference, PaymentStatus newStatus) {

        Payment payment = repository.findByOrderReference(orderReference)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(newStatus);

        repository.save(payment);
    }

    public PaymentResponse verifyOtp(VerifyOtpRequest request) {

        Payment payment = repository.findByOrderReference(request.getOrderReference())
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        ExpressCheckoutResponse response =
                smilePayClient.confirmZbPayment(
                        payment.getExternalReference(),
                        request.getOtp()
                );

        payment.setStatus(PaymentStatus.valueOf(response.getStatus()));
        repository.save(payment);

        return mapToResponse(payment);
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .orderReference(payment.getOrderReference())
                .status(payment.getStatus().name())
                .paymentCode(payment.getExternalReference())
                .message("Current status: " + payment.getStatus().name())
                .build();
    }
    public PaymentResponse checkPaymentStatus(String orderReference) {

        Payment payment = repository.findByOrderReference(orderReference)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getExternalReference() == null) {
            return PaymentResponse.builder()
                    .orderReference(orderReference)
                    .status(payment.getStatus().name())
                    .message("No gateway transaction created for this payment")
                    .build();
        }

        ExpressCheckoutResponse response =
                smilePayClient.checkStatus(payment.getExternalReference());

        payment.setStatus(PaymentStatus.valueOf(response.getStatus()));
        repository.save(payment);

        return PaymentResponse.builder()
                .orderReference(orderReference)
                .status(response.getStatus())
                .message(response.getResponseMessage())
                .build();
    }
        public PaymentResponse initiatePayment(InitiatePaymentRequest request) {

            if (repository.existsByOrderReference(request.getOrderReference())) {
                throw new RuntimeException("Duplicate order reference");
            }

            Payment payment = Payment.builder()
                    .orderReference(request.getOrderReference())
                    .amount(request.getAmount())
                    .currency("USD")
                    .method(PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase()))
                    .status(PaymentStatus.PENDING)
                    .customerPhone(request.getPhone())
                    .createdAt(LocalDateTime.now())
                    .build();

            repository.save(payment);

            ExpressCheckoutRequest gatewayRequest =
                    ExpressCheckoutRequest.builder()
                            .orderReference(payment.getOrderReference())
                            .amount(payment.getAmount().doubleValue())
                            .currencyCode("USD")
                            .mobilePhoneNumber(request.getPhone())
                            .email(request.getEmail())
                            .paymentMethod(request.getPaymentMethod())
                            .resultUrl("https://yourdomain.com/smilepay/api/webhook/smilepay")
                            .returnUrl("https://yourdomain.com/return")
                            .cancelUrl("https://yourdomain.com/cancel")
                            .failureUrl("https://yourdomain.com/failure")
                            .pan(request.getPan())
                            .expMonth(request.getExpMonth())
                            .expYear(request.getExpYear())
                            .securityCode(request.getSecurityCode())
                            .build();

            ExpressCheckoutResponse response =
                    smilePayClient.initiate(gatewayRequest);

            System.out.println("Gateway Response: " + response);
            payment.setExternalReference(response.getTransactionReference());
            repository.save(payment);

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

            if ("INNBUCKS".equalsIgnoreCase(request.getPaymentMethod())) {
                return PaymentResponse.builder()
                        .orderReference(payment.getOrderReference())
                        .status("PENDING")
                        .message(response.getResponseMessage())
                        .build();
            }

            return PaymentResponse.builder()
                    .orderReference(payment.getOrderReference())
                    .status(response.getStatus())
                    .build();
        }
    }