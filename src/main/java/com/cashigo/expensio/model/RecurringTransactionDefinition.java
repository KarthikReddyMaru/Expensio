package com.cashigo.expensio.model;

import com.cashigo.expensio.common.consts.TransactionRecurrence;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class RecurringTransactionDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String userId;

    private BigDecimal amount = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(nullable = false)
    private SubCategory subCategory;

    @Column(nullable = false, updatable = false)
    private TransactionRecurrence transactionRecurrenceType;

    private Instant lastProcessedInstant;

    @Column(nullable = false)
    private LocalDate nextOccurrence;

    private String note;

    @OneToMany(mappedBy = "transactionDefinition")
    private List<Transaction> transactions;

}
