package com.cashigo.expensio.repository;

import com.cashigo.expensio.model.BudgetDefinition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BudgetDefinitionRepository extends JpaRepository<BudgetDefinition, UUID> {

    Optional<BudgetDefinition> findBudgetDefinitionByIdAndUserId(UUID id, String userId);

    List<BudgetDefinition> findBudgetDefinitionsByUserId(String userId, Pageable pageable);

    void deleteBudgetDefinitionByIdAndUserId(UUID id, String userId);
    
}
