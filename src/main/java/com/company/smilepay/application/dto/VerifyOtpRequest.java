package com.company.smilepay.application.dto;

import lombok.Data;

@Data
public class VerifyOtpRequest {

    private String orderReference;
    private String otp;
}