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
                .customerPhone(request.getPhone())
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(payment);

        ExpressCheckoutResponse response;

        if (method == PaymentMethod.OMARI ||
                method == PaymentMethod.WALLETPLUS) {

            ExpressCheckoutRequest zbRequest = builder()
                    .orderReference(request.getOrderReference())
                    .amount(payment.getAmount().doubleValue())
                    .returnUrl("https://yourdomain.com/return")
                    .resultUrl("https://yourdomain.com/api/webhook/smilepay")
                    .itemName("Payment")
                    .itemDescription("SmilePay Payment")
                    .currencyCode("924")
                    .firstName("Test")
                    .lastName("User")
                    .mobilePhoneNumber(request.getPhone())
                    .email("test@test.com")
                    .paymentMethod(request.getPaymentMethod())
                    .cancelUrl("https://yourdomain.com/cancel")
                    .failureUrl("https://yourdomain.com/failure")
                    .build();

            response = smilePayClient.initiateZbPayment(zbRequest);

        } else {

            ExpressCheckoutRequest gatewayRequest =
                    builder()
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

            response = smilePayClient.initiate(gatewayRequest);
        }

        System.out.println("Gateway Response: " + response);

        // Save transaction reference safely
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

        return PaymentResponse.builder()
                .orderReference(payment.getOrderReference())
                .status(response.getStatus())
                .message(response.getResponseMessage())
                .build();
    }

    public PaymentResponse verifyOtp(VerifyOtpRequest request) {

        Payment payment = repository.findByOrderReference(request.getOrderReference())
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getExternalReference() == null) {
            throw new RuntimeException("Transaction not created. Cannot verify OTP.");
        }

        ExpressCheckoutResponse response =
                smilePayClient.confirmZbPayment(
                        payment.getExternalReference(),
                        request.getOtp()
                );

        payment.setStatus(PaymentStatus.valueOf(response.getStatus()));
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
                .paymentCode(payment.getExternalReference())
                .message("Current status: " + payment.getStatus().name())
                .build();
    }
}