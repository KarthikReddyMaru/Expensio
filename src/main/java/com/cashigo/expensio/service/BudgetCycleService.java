package com.cashigo.expensio.service;

import com.cashigo.expensio.model.BudgetCycle;
import com.cashigo.expensio.repository.BudgetCycleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BudgetCycleService {

    private final BudgetCycleRepository budgetCycleRepository;

    public BudgetCycle getActiveBudgetCycleByBudgetDefinition(UUID budgetDefinitionId) {
        return budgetCycleRepository.findActiveBudgetCycleByBudgetDefinition_Id(budgetDefinitionId);
    }

    public void saveBudgetCycleById(BudgetCycle budgetCycle) {
        budgetCycleRepository.save(budgetCycle);
    }

}
