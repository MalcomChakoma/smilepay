package com.company.smilepay.interfaces.rest;

import com.company.smilepay.application.dto.WebhookRequest;
import com.company.smilepay.application.service.PaymentService;
import com.company.smilepay.domain.model.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final PaymentService service;

    @PostMapping("/smilepay")
    public ResponseEntity<?> handleWebhook(@RequestBody WebhookRequest request) {

        PaymentStatus status = PaymentStatus.valueOf(request.getStatus());

        service.updateStatus(request.getOrderReference(), status);

        return ResponseEntity.ok().build();
    }
}