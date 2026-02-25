package com.company.smilepay.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SpringDataPaymentRepository
        extends JpaRepository<PaymentEntity, Long> {

    Optional<PaymentEntity> findByOrderReference(String orderReference);

    boolean existsByOrderReference(String orderReference);
}