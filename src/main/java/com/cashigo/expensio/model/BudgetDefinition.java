package com.cashigo.expensio.model;

import com.cashigo.expensio.common.consts.BudgetRecurrence;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        uniqueConstraints =
                @UniqueConstraint(name = "userId_Category_unq", columnNames = {"userId", "category_id"}),
        indexes = {
                @Index(name = "userId_idx", columnList = "user_id"),
                @Index(name = "userId_category_idx", columnList = "user_id, category_id"),
        }
)
@Data
@ToString(exclude = "budgetCycles")
public class BudgetDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String userId;

    @ManyToOne
    @JoinColumn(updatable = false, name = "category_id")
    private Category category;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal budgetAmount;

    @Enumerated(EnumType.STRING)
    @Column(updatable = false, nullable = false)
    private BudgetRecurrence budgetRecurrenceType;

    @OneToMany(mappedBy = "budgetDefinition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BudgetCycle> budgetCycles;

}
