package com.cashigo.expensio.repository;

import com.cashigo.expensio.model.RecurringTransactionDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RecurringTransactionDefinitionRepository extends JpaRepository<RecurringTransactionDefinition, UUID> {

}
