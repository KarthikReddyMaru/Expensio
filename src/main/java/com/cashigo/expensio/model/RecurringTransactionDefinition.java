package com.cashigo.expensio.model;

import com.cashigo.expensio.common.consts.TransactionRecurrence;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@ToString(exclude = {"transactions"})
@Table(
        name = "RecurringTransactions",
        indexes =
                @Index(name = "next_occurrence_idx", columnList = "nextOccurrence")
)
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
    @Enumerated(EnumType.STRING)
    private TransactionRecurrence transactionRecurrenceType;

    private Instant lastProcessedInstant;

    @Column(nullable = false)
    private LocalDate nextOccurrence;

    private String note;

    @OneToMany(mappedBy = "transactionDefinition")
    private List<Transaction> transactions;

}
