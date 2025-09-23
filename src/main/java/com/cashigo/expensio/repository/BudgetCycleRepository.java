package com.cashigo.expensio.repository;

import com.cashigo.expensio.model.BudgetCycle;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BudgetCycleRepository extends JpaRepository<BudgetCycle, UUID> {

    Optional<BudgetCycle> findBudgetCycleByBudgetCycleIdAndBudgetDefinition_UserId(UUID budgetCycleId, String budgetDefinitionUserId);

    @Query("select c from BudgetCycle c where c.isActive = true and c.budgetDefinition.userId = :userId")
    List<BudgetCycle> findActiveBudgetCyclesByUserId(String userId);

    @Query("select c from BudgetCycle c " +
            "where c.isActive = true and c.budgetDefinition.category.id = :categoryId and c.budgetDefinition.userId = :userId")
    Optional<BudgetCycle> findActiveBudgetCycleByCategory(String userId, Long categoryId);
    
    List<BudgetCycle> findBudgetCyclesByBudgetDefinition_Id(UUID budgetDefinitionId, Sort sort);

    @Query("select c from BudgetCycle c where c.isActive = true and c.budgetDefinition.id = :budgetDefinitionId")
    BudgetCycle findActiveBudgetCycleByBudgetDefinition_Id(UUID budgetDefinitionId);
}
