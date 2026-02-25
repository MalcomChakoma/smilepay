package com.company.smilepay.infrastructure.config;

import com.company.smilepay.domain.repository.PaymentRepository;
import com.company.smilepay.infrastructure.persistence.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public PaymentRepository paymentRepository(SpringDataPaymentRepository repo) {
        return new PaymentRepositoryAdapter(repo);
    }
}