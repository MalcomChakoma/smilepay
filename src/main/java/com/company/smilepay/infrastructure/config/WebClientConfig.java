package com.company.smilepay.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://zbnet.zb.co.zw/wallet_sandbox_api/payments-gateway")
                .defaultHeader("x-api-key", "81c50549-27d9-4050-96b1-177ef89bd729")
                .defaultHeader("x-api-secret", "f705cf9d-0d37-405d-b512-e2c80ea721ab")
                .build();
    }
}