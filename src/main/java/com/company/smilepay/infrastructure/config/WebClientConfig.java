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
                .defaultHeader("x-api-key", "3cf68886-16bb-4035-9b8d-16c8d4b88489")
                .defaultHeader("x-api-secret", "b47880c3-76f3-49e4-96f9-14627b300755")
                .build();
    }
}