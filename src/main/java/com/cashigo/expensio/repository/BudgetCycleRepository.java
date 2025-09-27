package com.cashigo.expensio.repository;

import com.cashigo.expensio.model.BudgetCycle;
import com.cashigo.expensio.model.Transaction;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BudgetCycleRepository extends JpaRepository<BudgetCycle, UUID> {

    @Query("select bc from BudgetCycle bc where bc.id = :budgetCycleId and bc.budgetDefinition.userId = :userId")
    Optional<BudgetCycle> findBudgetCycleById(UUID budgetCycleId, String userId);

    @Query("select bc from BudgetCycle bc left join fetch bc.transactions where bc.id = :budgetCycleId")
    Optional<BudgetCycle> findBudgetCycleWithTransactionsByCycleId(UUID budgetCycleId);

    @Query("""
        select distinct bc from BudgetCycle bc
            left join fetch bc.transactions t
            left join fetch t.subCategory sc
            left join fetch sc.category
        where bc.id = :budgetCycleId and bc.budgetDefinition.userId = :userId
    """)
    Optional<BudgetCycle> findBudgetCycleWithTransactionsByCycleId(UUID budgetCycleId, String userId);
}
