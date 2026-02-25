package com.company.smilepay.interfaces.rest;

import com.company.smilepay.application.dto.*;
import com.company.smilepay.application.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

        private final PaymentService service;

        @PostMapping("/initiate")
        public PaymentResponse initiate(@RequestBody InitiatePaymentRequest request) {
            return service.initiatePayment(request);
        }

        @PostMapping("/verify-otp")
        public PaymentResponse verifyOtp(@RequestBody VerifyOtpRequest request) {
            return service.verifyOtp(request);
        }

        @GetMapping("/status/check/{orderReference}")
        public PaymentResponse checkStatus(@PathVariable String orderReference) {
            return service.checkPaymentStatus(orderReference);
        }
    }