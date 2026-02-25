package com.company.smilepay.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String orderReference;

    private BigDecimal amount;
    private String currency;
    private String method;
    private String status;
    private String customerPhone;
    private LocalDateTime createdAt;
    @Column
    private String externalReference;
}