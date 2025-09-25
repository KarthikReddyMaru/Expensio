package com.cashigo.expensio.repository;

import com.cashigo.expensio.model.RecurringTransactionDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface RecurringTransactionDefinitionRepository extends JpaRepository<RecurringTransactionDefinition, UUID> {
    List<RecurringTransactionDefinition> findRecurringTransactionDefinitionsByNextOccurrenceEquals(LocalDate nextOccurrence);
}
