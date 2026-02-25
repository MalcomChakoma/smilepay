package com.company.smilepay.application.dto;

import lombok.Data;

@Data
public class WebhookRequest {

    private String orderReference;
    private String status;
}