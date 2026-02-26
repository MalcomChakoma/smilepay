package com.company.smilepay.domain.repository;

import com.company.smilepay.domain.model.Payment;
import java.util.Optional;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findByOrderReference(String orderReference);

    boolean existsByOrderReference(String orderReference);

}