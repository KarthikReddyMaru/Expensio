package com.cashigo.expensio.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        indexes = {
                @Index(name = "cycleStartDate_idx", columnList = "cycleStartDateTime"),
                @Index(name = "cycleEndDate_idx", columnList = "cycleEndDateTime"),
                @Index(name = "currentActiveCycle", columnList = "isActive")
        }
)
@Data
public class BudgetCycle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID budgetCycleId;

    @ManyToOne
    private BudgetDefinition budgetDefinition;

    private Instant cycleStartDateTime;

    private Instant cycleEndDateTime;

    private boolean isActive;

    @OneToMany(mappedBy = "budgetCycle")
    private List<Transaction> transactions;

}
