package com.cashigo.expensio.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
        indexes = {
                @Index(name = "user_id_idx", columnList = "userId"),
                @Index(name = "sub_cat_idx", columnList = "sub_category_id")
        }
)
@Data
@ToString(exclude = {""})
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(nullable = false)
    private SubCategory subCategory;

    @Column(nullable = false)
    private Instant transactionDateTime;

    private String note;

    @ManyToOne
    @JoinColumn(updatable = false)
    private RecurringTransactionDefinition transactionDefinition;

    @ManyToOne
    @JoinColumn
    private BudgetCycle budgetCycle;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(insertable = false)
    private Instant updatedAt;

}
