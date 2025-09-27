package com.cashigo.expensio.service;

import com.cashigo.expensio.common.consts.TransactionRecurrence;
import com.cashigo.expensio.common.security.UserContext;
import com.cashigo.expensio.dto.exception.InvalidRecurrenceTransactionException;
import com.cashigo.expensio.model.RecurringTransactionDefinition;
import com.cashigo.expensio.model.Transaction;
import com.cashigo.expensio.repository.RecurringTransactionDefinitionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecurringTransactionService {
    
    @Value("${zone.id}")
    private String zone;
    private final RecurringTransactionDefinitionRepository transactionDefinitionRepository;
    private final UserContext userContext;

    @Transactional
    public RecurringTransactionDefinition createRecurringTransactionDefinition(Transaction transaction, TransactionRecurrence transactionRecurrence) {
        RecurringTransactionDefinition recurringTransactionDefinition = mapToTransactionDefinition(transaction, transactionRecurrence);
        ZoneId zoneId = ZoneId.of(zone);
        Instant lastProcessedInstant = recurringTransactionDefinition.getLastProcessedInstant();
        LocalDate nextOccurrence = null;
        switch (transactionRecurrence) {
            case DAILY -> nextOccurrence = lastProcessedInstant.atZone(zoneId).plusDays(1).toLocalDate();
            case WEEKLY -> nextOccurrence = lastProcessedInstant.atZone(zoneId).plusWeeks(1).toLocalDate();
        }
        recurringTransactionDefinition.setNextOccurrence(nextOccurrence);
        return transactionDefinitionRepository.save(recurringTransactionDefinition);
    }

    @Transactional
    public void deleteRecurringTransactionDefinition(UUID definitionId) {
        String userId = userContext.getUserId();
        transactionDefinitionRepository.deleteByIdAndUserId(definitionId, userId);
    }

    private RecurringTransactionDefinition mapToTransactionDefinition(Transaction transaction, TransactionRecurrence transactionRecurrence) {
        RecurringTransactionDefinition recurringTransactionDefinition = new RecurringTransactionDefinition();
        recurringTransactionDefinition.setAmount(transaction.getAmount());
        recurringTransactionDefinition.setUserId(transaction.getUserId());
        recurringTransactionDefinition.setSubCategory(transaction.getSubCategory());
        recurringTransactionDefinition.setTransactionRecurrenceType(transactionRecurrence);
        recurringTransactionDefinition.setNote(transaction.getNote());
        if (isValidRecurrenceTransaction(transaction.getTransactionDateTime()))
            recurringTransactionDefinition.setLastProcessedInstant(transaction.getTransactionDateTime());
        return recurringTransactionDefinition;
    }

    private boolean isValidRecurrenceTransaction(Instant transactionInstant) {
        ZoneId zoneId = ZoneId.of(zone);
        LocalDate today = LocalDate.now(zoneId);
        LocalDate transactionDate = transactionInstant.atZone(zoneId).toLocalDate();
        if (transactionDate.isBefore(today))
            throw new InvalidRecurrenceTransactionException("A recurring transaction cannot have a past date");
        return true;
    }
}
