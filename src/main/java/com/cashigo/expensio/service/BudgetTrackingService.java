package com.cashigo.expensio.service;

import com.cashigo.expensio.common.security.UserContext;
import com.cashigo.expensio.model.BudgetCycle;
import com.cashigo.expensio.model.SubCategory;
import com.cashigo.expensio.model.Transaction;
import com.cashigo.expensio.repository.BudgetCycleRepository;
import com.cashigo.expensio.repository.BudgetDefinitionRepository;
import com.cashigo.expensio.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetTrackingService {

    private final TransactionRepository transactionRepository;
    private final SubCategoryService subCategoryService;

    public void addPreviousTransactionsToCurrentBudgetCycle(BudgetCycle budgetCycle) {
        Long categoryId = budgetCycle.getBudgetDefinition().getCategory().getId();
        Instant cycleStartDate = budgetCycle.getCycleStartDateTime();
        Instant cycleEndDate = budgetCycle.getCycleEndDateTime();
        List<SubCategory> subCategories = subCategoryService.getSubCategoryEntities(categoryId);
        List<Long> subCategoryIds = subCategories.stream().map(SubCategory::getId).toList();
        String userId = budgetCycle.getBudgetDefinition().getUserId();
        List<Transaction> transactions = transactionRepository
                .findTransactionsByInstantRangeWithSubCategories(userId, subCategoryIds, cycleStartDate, cycleEndDate);
        transactions.forEach(transaction -> transaction.setBudgetCycle(budgetCycle));
        transactionRepository.saveAll(transactions);
    }

}
