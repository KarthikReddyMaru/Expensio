package com.cashigo.expensio.service;

import com.cashigo.expensio.common.security.UserContext;
import com.cashigo.expensio.dto.SubCategoryDto;
import com.cashigo.expensio.model.BudgetCycle;
import com.cashigo.expensio.model.BudgetDefinition;
import com.cashigo.expensio.model.Transaction;
import com.cashigo.expensio.repository.BudgetCycleRepository;
import com.cashigo.expensio.repository.BudgetDefinitionRepository;
import com.cashigo.expensio.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetTrackingService {

    private final BudgetCycleRepository budgetCycleRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetDefinitionRepository budgetDefinitionRepository;
    private final SubCategoryService subCategoryService;
    private final UserContext userContext;


    public BigDecimal addPreviousTransactionsAmountToCurrentBudgetCycle(BudgetCycle budgetCycle) {
        Instant cycleStartDate = budgetCycle.getCycleStartDateTime();
        Instant cycleEndDate = budgetCycle.getCycleEndDateTime();
        String userId = userContext.getUserId();
        List<Transaction> transactions = transactionRepository.findTransactionsByUserIdAndTransactionDateTimeBetween(userId, cycleStartDate, cycleEndDate);
        return transactions.stream().map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addAmountSpentInCurrentBudgetCycle(Long subCategoryId, BigDecimal amountSpent) {
        String userId = userContext.getUserId();
        SubCategoryDto subCategory = subCategoryService.getSubCategoryById(subCategoryId);
        Long categoryId = subCategory.getCategoryId();
        BudgetDefinition budgetDefinition = budgetDefinitionRepository
                .findBudgetDefinitionByCategory_IdAndUserId(categoryId, userId).orElse(null);
        if (budgetDefinition != null) {
            UUID budgetDefinitionId = budgetDefinition.getId();
            BudgetCycle activeCycle = budgetCycleRepository.findActiveBudgetCycleByBudgetDefinition_Id(budgetDefinitionId);
            BigDecimal amountSpentTillNow = activeCycle.getAmountSpent();
            activeCycle.setAmountSpent(amountSpentTillNow.add(amountSpent));
            budgetCycleRepository.save(activeCycle);
            log.info("Budget Amount updated for cycle ({})", activeCycle.getBudgetCycleId());
        }
    }

}
