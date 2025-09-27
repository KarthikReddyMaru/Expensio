package com.cashigo.expensio.repository;

import com.cashigo.expensio.model.RecurringTransactionDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecurringTransactionDefinitionRepository extends JpaRepository<RecurringTransactionDefinition, UUID> {

    @Query("""
            select rt from RecurringTransactionDefinition rt
            join fetch rt.subCategory sc
            join fetch sc.category
            where rt.id = :id and rt.userId = :userId
    """)
    Optional<RecurringTransactionDefinition> findByIdAndUserId(UUID id, String userId);

    List<RecurringTransactionDefinition> findByUserId(String userId);
    
    List<RecurringTransactionDefinition> findRecurringTransactionDefinitionsByNextOccurrenceEquals(LocalDate nextOccurrence);

    void deleteByIdAndUserId(UUID id, String userId);
    
}
