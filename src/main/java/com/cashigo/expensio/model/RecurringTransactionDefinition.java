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
        indexes = {
                @Index(name = "next_occurrence_idx", columnList = "nextOccurrence"),
                @Index(name = "user_id_idx", columnList = "user_id")
        }
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
    @JoinColumn(foreignKey =
        @ForeignKey(
                name = "fk_subcategory",
                foreignKeyDefinition = "FOREIGN KEY (sub_category_id) REFERENCES sub_category(id) ON DELETE CASCADE"
        )
    )
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
