package com.cashigo.expensio.scheduler;

import com.cashigo.expensio.model.BudgetCycle;
import com.cashigo.expensio.model.RecurringTransactionDefinition;
import com.cashigo.expensio.model.Transaction;
import com.cashigo.expensio.repository.RecurringTransactionDefinitionRepository;
import com.cashigo.expensio.repository.TransactionRepository;
import com.cashigo.expensio.service.BudgetCycleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecurringTransactionScheduler {

    @Value("${zone.id}")
    private String zone;

    private final RecurringTransactionDefinitionRepository transactionDefinitionRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetCycleService budgetCycleService;

//    @Scheduled(cron = "0 */1 * * * *")
    @Transactional
    public void saveRecurringTransaction() {
        log.info("Recurring Transactions...");
        ZoneId zoneId = ZoneId.of(zone);
        LocalDate today = LocalDate.now(zoneId);
        List<RecurringTransactionDefinition> recurringTransactionDefinitions =
                transactionDefinitionRepository.findRecurringTransactionDefinitionsByNextOccurrenceEquals(today);
        List<Transaction> newTransactions = recurringTransactionDefinitions
                .stream()
                .map(this::mapToTransaction)
                .toList();
        transactionRepository.saveAll(newTransactions);
        transactionDefinitionRepository.saveAll(recurringTransactionDefinitions);
        log.info("Recurring Transactions Ends...");
    }

    public Transaction mapToTransaction(RecurringTransactionDefinition recurringTransactionDefinition) {
        Transaction transaction = new Transaction();
        transaction.setTransactionDefinition(recurringTransactionDefinition);
        transaction.setUserId(recurringTransactionDefinition.getUserId());
        transaction.setAmount(recurringTransactionDefinition.getAmount());
        transaction.setSubCategory(recurringTransactionDefinition.getSubCategory());
        transaction.setNote(recurringTransactionDefinition.getNote());
        Instant lastProcessedInstant = recurringTransactionDefinition.getLastProcessedInstant();
        ZoneId zoneId = ZoneId.of(zone);
        ZonedDateTime zonedLastProcessedDateTime = lastProcessedInstant.atZone(zoneId);
        LocalDate lastOccurrenceDate = recurringTransactionDefinition.getNextOccurrence();
        Instant currProcessedInstant = null;
        LocalDate nextProcessedDate = null;
        switch (recurringTransactionDefinition.getTransactionRecurrenceType()) {
            case DAILY -> {
                currProcessedInstant = zonedLastProcessedDateTime.plusDays(1).toInstant();
                nextProcessedDate = lastOccurrenceDate.plusDays(1);
            }
            case WEEKLY -> {
                currProcessedInstant = zonedLastProcessedDateTime.plusWeeks(1).toInstant();
                nextProcessedDate = lastOccurrenceDate.plusWeeks(1);
            }
        }
        transaction.setTransactionDateTime(currProcessedInstant);
        recurringTransactionDefinition.setLastProcessedInstant(currProcessedInstant);
        recurringTransactionDefinition.setNextOccurrence(nextProcessedDate);
        Long subCategoryId = recurringTransactionDefinition.getSubCategory().getId();
        String userId = recurringTransactionDefinition.getUserId();
        BudgetCycle activeCycle = budgetCycleService.getActiveBudgetCycleBySubCategoryId(subCategoryId, userId);
        log.info("Active cycle {}", activeCycle != null ? activeCycle.getId() : "What the");
        if (activeCycle != null)
            transaction.setBudgetCycle(activeCycle);
        return transaction;
    }

}
