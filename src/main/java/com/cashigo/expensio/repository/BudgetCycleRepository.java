package com.cashigo.expensio.repository;

import com.cashigo.expensio.model.BudgetCycle;
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

    @Query("select c from BudgetCycle c where c.isActive = true and c.budgetDefinition.userId = :userId")
    List<BudgetCycle> findActiveBudgetCyclesByUserId(String userId);

    @Query("select c from BudgetCycle c " +
            "where c.isActive = true and c.budgetDefinition.category.id = :categoryId and c.budgetDefinition.userId = :userId")
    Optional<BudgetCycle> findActiveBudgetCycleByCategory(String userId, Long categoryId);

    @Query("select c from BudgetCycle c where c.isActive = true and c.budgetDefinition.id = :budgetDefinitionId")
    BudgetCycle findActiveBudgetCycleByBudgetDefinition_Id(UUID budgetDefinitionId);
}
