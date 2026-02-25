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

        if (request.getOrderReference() == null || request.getStatus() == null) {
            return ResponseEntity.badRequest().body("Missing required fields");
        }

        PaymentStatus status;
        try {
            status = PaymentStatus.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status value");
        }

        service.updateStatus(request.getOrderReference(), status);

        return ResponseEntity.ok().build();
    }
}