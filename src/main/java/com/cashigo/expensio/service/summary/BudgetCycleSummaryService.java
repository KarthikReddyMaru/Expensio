package com.cashigo.expensio.service.summary;

import com.cashigo.expensio.dto.exception.NoBudgetCycleFoundException;
import com.cashigo.expensio.dto.summary.BudgetCycleSummaryDto;
import com.cashigo.expensio.dto.summary.mapper.BudgetCycleToSummaryMapper;
import com.cashigo.expensio.model.BudgetCycle;
import com.cashigo.expensio.model.Transaction;
import com.cashigo.expensio.repository.BudgetCycleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BudgetCycleSummaryService {

    private final BudgetCycleRepository budgetCycleRepository;
    private final BudgetCycleToSummaryMapper budgetCycleToSummaryMapper;

    public BudgetCycleSummaryDto getBudgetCycleSummaryById(UUID budgetCycleId) {
        BudgetCycle budgetCycle = budgetCycleRepository
                .findById(budgetCycleId)
                .orElseThrow(NoBudgetCycleFoundException::new);
        BudgetCycleSummaryDto budgetCycleDto = budgetCycleToSummaryMapper.map(budgetCycle);
        BigDecimal amountSpent = getAmountSpentInCycle(budgetCycleId);
        budgetCycleDto.setAmountSpent(amountSpent);
        return budgetCycleDto;
    }

    public BigDecimal getAmountSpentInCycle(UUID budgetCycleId) {
        BudgetCycle transactions = budgetCycleRepository
                .findTransactionsByCycleId(budgetCycleId)
                .orElseThrow(NoBudgetCycleFoundException::new);
        return transactions
                .getTransactions()
                .stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
