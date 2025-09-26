package com.cashigo.expensio.repository;

import com.cashigo.expensio.common.consts.BudgetRecurrence;
import com.cashigo.expensio.model.BudgetDefinition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BudgetDefinitionRepository extends JpaRepository<BudgetDefinition, UUID> {

    Optional<BudgetDefinition> findBudgetDefinitionByIdAndUserId(UUID id, String userId);

    Optional<BudgetDefinition> findBudgetDefinitionByCategory_IdAndUserId(Long categoryId, String userId);

    List<BudgetDefinition> findBudgetDefinitionsByUserId(String userId);

    List<BudgetDefinition> findBudgetDefinitionsByBudgetRecurrenceType(BudgetRecurrence budgetRecurrenceType);

    @Query("select distinct bd from BudgetDefinition bd join fetch bd.budgetCycles where bd.userId = :userId")
    List<BudgetDefinition> findUserBudgetDefinitionsWithCycles(String userId);

    void deleteBudgetDefinitionByIdAndUserId(UUID id, String userId);
    
}
