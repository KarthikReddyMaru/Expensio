package com.cashigo.expensio.scheduler;

import com.cashigo.expensio.common.consts.BudgetRecurrence;
import com.cashigo.expensio.model.BudgetCycle;
import com.cashigo.expensio.model.BudgetDefinition;
import com.cashigo.expensio.repository.BudgetDefinitionRepository;
import com.cashigo.expensio.service.BudgetCycleService;
import com.cashigo.expensio.service.BudgetDefinitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetRefreshScheduler {

    private final BudgetDefinitionRepository budgetDefinitionRepository;
    private final BudgetDefinitionService budgetDefinitionService;
    private final BudgetCycleService budgetCycleService;

    @Value("${zone.id}")
    private String zone;

    @Scheduled(cron = "0 */2 * * * *")
    @Transactional
    public void refreshWeeklyCycles() {
        log.info("Scheduler starts...");
        List<BudgetDefinition> weeklyBudgetDefinitions =
                budgetDefinitionRepository.findBudgetDefinitionsByBudgetRecurrenceType(BudgetRecurrence.WEEKLY);
        List<BudgetDefinition> refreshedBudgetDefinitions =  weeklyBudgetDefinitions
                .stream()
                .peek(budget -> {
                    List<BudgetCycle> budgetCycles = budget.getBudgetCycles();
                    UUID budgetDefinitionId = budget.getId();
                    BudgetCycle currentActiveCycle = budgetCycleService.getActiveBudgetCycleByBudgetDefinition(budgetDefinitionId);
                    currentActiveCycle.setActive(false);
                    BudgetCycle newBudgetCycle = createBudgetCycle(budget);
                    budgetCycles.add(newBudgetCycle);
                })
                .toList();
        budgetDefinitionRepository.saveAll(refreshedBudgetDefinitions);
        log.info("Scheduler ends.." );
    }

    public BudgetCycle createBudgetCycle(BudgetDefinition budgetDefinition) {
        ZoneId zoneId = ZoneId.of(zone);
        LocalDate currDate = LocalDate.now(zoneId);
        LocalDate cycleStartDate = currDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate cycleEndDate = currDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return budgetDefinitionService.createCycle(cycleStartDate, zoneId, cycleEndDate, budgetDefinition);
    }

}
