package com.cashigo.expensio.scheduler;

import com.cashigo.expensio.common.consts.BudgetRecurrence;
import com.cashigo.expensio.model.BudgetCycle;
import com.cashigo.expensio.model.BudgetDefinition;
import com.cashigo.expensio.repository.BudgetCycleRepository;
import com.cashigo.expensio.repository.BudgetDefinitionRepository;
import com.cashigo.expensio.service.BudgetCycleService;
import com.cashigo.expensio.service.BudgetDefinitionService;
import com.cashigo.expensio.service.BudgetTrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
//@Service
@RequiredArgsConstructor
public class BudgetRefreshScheduler {

    private final BudgetDefinitionRepository budgetDefinitionRepository;
    private final BudgetCycleRepository budgetCycleRepository;
    private final BudgetCycleService budgetCycleService;
    private final BudgetTrackingService budgetTrackingService;

    @Value("${zone.id}")
    private String zone;

    @Scheduled(cron = "0 0 1 * * Mon")
    @Transactional
    public void refreshWeeklyCycles() {
        log.info("Scheduler starts...");

        List<BudgetDefinition> weeklyBudgetDefinitions =
                budgetDefinitionRepository.findBudgetDefinitionsByBudgetRecurrenceTypeWithCycles(BudgetRecurrence.WEEKLY);

        List<BudgetCycle> previousActiveCycles = weeklyBudgetDefinitions
                .stream()
                .map(budgetCycleService::getActiveBudgetCycleByBudgetDefinition)
                .peek(cycle -> cycle.setActive(false))
                .toList();
        budgetCycleRepository.saveAll(previousActiveCycles);

        List<BudgetCycle> refreshedBudgetCycles = weeklyBudgetDefinitions
                .stream()
                .map(budgetCycleService::createBudgetCycle)
                .toList();
        List<BudgetCycle> budgetCycles = budgetCycleRepository.saveAll(refreshedBudgetCycles);

        budgetCycles.forEach(budgetTrackingService::addPreviousTransactionsToCurrentBudgetCycle);

        log.info("Scheduler ends..");
    }

}
