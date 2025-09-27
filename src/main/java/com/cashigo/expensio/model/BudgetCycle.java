package com.cashigo.expensio.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        uniqueConstraints =
                @UniqueConstraint(name = "no_duplicate_cycle_unq", columnNames =
                        {"cycle_start_date_time", "cycle_end_date_time", "budget_definition_id"}
                ),
        indexes = {
                @Index(name = "cycleStartDate_idx", columnList = "cycleStartDateTime"),
                @Index(name = "cycleEndDate_idx", columnList = "cycleEndDateTime"),
                @Index(name = "currentActiveCycle", columnList = "isActive")
        }
)
@Data
@ToString(exclude = {"transactions"})
public class BudgetCycle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private BudgetDefinition budgetDefinition;

    private Instant cycleStartDateTime;

    private Instant cycleEndDateTime;

    private boolean isActive;

    @OneToMany(mappedBy = "budgetCycle")
    private List<Transaction> transactions;

}
