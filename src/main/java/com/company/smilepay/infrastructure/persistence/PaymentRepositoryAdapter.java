package com.company.smilepay.infrastructure.persistence;

import com.company.smilepay.domain.model.*;
import com.company.smilepay.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final SpringDataPaymentRepository jpaRepository;

    @Override
    public Payment save(Payment payment) {

        PaymentEntity entity = jpaRepository
                .findByOrderReference(payment.getOrderReference())
                .orElse(new PaymentEntity());

        entity.setOrderReference(payment.getOrderReference());
        entity.setAmount(payment.getAmount());
        entity.setCurrency(payment.getCurrency());
        entity.setMethod(payment.getMethod().name());
        entity.setStatus(payment.getStatus().name());
        entity.setCustomerPhone(payment.getCustomerPhone());
        entity.setCreatedAt(payment.getCreatedAt());
        entity.setExternalReference(payment.getExternalReference());

        jpaRepository.save(entity);

        return payment;
    }


    @Override
    public Optional<Payment> findByOrderReference(String orderReference) {

        return jpaRepository.findByOrderReference(orderReference)
                .map(entity -> Payment.builder()
                        .orderReference(entity.getOrderReference())
                        .amount(entity.getAmount())
                        .currency(entity.getCurrency())
                        .method(PaymentMethod.valueOf(entity.getMethod()))
                        .status(PaymentStatus.valueOf(entity.getStatus()))
                        .customerPhone(entity.getCustomerPhone())
                        .createdAt(entity.getCreatedAt())
                        .externalReference(entity.getExternalReference())
                        .build());
    }

    @Override
    public boolean existsByOrderReference(String orderReference) {
        return jpaRepository.existsByOrderReference(orderReference);
    }
}