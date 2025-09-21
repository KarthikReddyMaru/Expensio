package com.cashigo.expensio.model;

import com.cashigo.expensio.common.consts.Recurrence;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        indexes = {
                @Index(name = "userId_idx", columnList = "userId"),
                @Index(name = "category_idx", columnList = "category")
        }
)
@Data
public class BudgetDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String userId;

    @ManyToOne
    private Category category;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal budgetAmount;

    @Enumerated(EnumType.STRING)
    private Recurrence recurrenceType;

    @OneToMany(mappedBy = "budgetDefinition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BudgetCycle> budgetCycles;

}
