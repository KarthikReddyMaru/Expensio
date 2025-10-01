package com.cashigo.expensio.repository;

import com.cashigo.expensio.common.consts.BudgetRecurrence;
import com.cashigo.expensio.model.BudgetDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BudgetDefinitionRepository extends JpaRepository<BudgetDefinition, UUID> {

    Optional<BudgetDefinition> findBudgetDefinitionByIdAndUserId(UUID id, String userId);

    @Query("select distinct bd from BudgetDefinition bd join fetch bd.budgetCycles where bd.userId = :userId and bd.category.id = :categoryId")
    Optional<BudgetDefinition> findBudgetDefinitionByCategoryWithCycles(Long categoryId, String userId);

    @Query("""
        select distinct bd from BudgetDefinition bd
            join fetch bd.budgetCycles bc
        where bd.userId = :userId and bd.category.id = :categoryId and :instant between bc.cycleStartDateTime and bc.cycleEndDateTime
    """)
    Optional<BudgetDefinition> findBudgetDefinitionByCategoryAndDateRangeWithCycles(Long categoryId, Instant instant, String userId);

    List<BudgetDefinition> findBudgetDefinitionsByUserId(String userId);

    @Query("select distinct bd from BudgetDefinition bd join fetch bd.budgetCycles where bd.budgetRecurrenceType = :budgetRecurrenceType")
    List<BudgetDefinition> findBudgetDefinitionsByBudgetRecurrenceTypeWithCycles(BudgetRecurrence budgetRecurrenceType);

    @Query("select distinct bd from BudgetDefinition bd join fetch bd.budgetCycles where bd.userId = :userId")
    List<BudgetDefinition> findBudgetDefinitionsWithCycles(String userId);

    @Query("select distinct bd from BudgetDefinition bd join fetch bd.budgetCycles where bd.userId = :userId and bd.id = :id")
    Optional<BudgetDefinition> findBudgetDefinitionByIdWithCycles(UUID id, String userId);

    void deleteBudgetDefinitionByIdAndUserId(UUID id, String userId);
    
}
