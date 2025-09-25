package com.cashigo.expensio.service;

import com.cashigo.expensio.common.consts.TransactionRecurrence;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class RecurringTransactionService {
    
    @Value("${zone.id}")
    private String zone;
    private final RecurringTransactionDefinitionRepository transactionDefinitionRepository;

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
        RecurringTransactionDefinition saved = transactionDefinitionRepository.save(recurringTransactionDefinition);
        log.info("Saved recurring transaction {}", recurringTransactionDefinition);
        return saved;
    }

    private static RecurringTransactionDefinition mapToTransactionDefinition(Transaction transaction, TransactionRecurrence transactionRecurrence) {
        RecurringTransactionDefinition recurringTransactionDefinition = new RecurringTransactionDefinition();
        recurringTransactionDefinition.setAmount(transaction.getAmount());
        recurringTransactionDefinition.setUserId(transaction.getUserId());
        recurringTransactionDefinition.setSubCategory(transaction.getSubCategory());
        recurringTransactionDefinition.setTransactionRecurrenceType(transactionRecurrence);
        recurringTransactionDefinition.setNote(transaction.getNote());
        recurringTransactionDefinition.setLastProcessedInstant(transaction.getTransactionDateTime());
        return recurringTransactionDefinition;
    }
}
