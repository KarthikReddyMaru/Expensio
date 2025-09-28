package com.cashigo.expensio.service;

import com.cashigo.expensio.common.consts.BudgetRecurrence;
import com.cashigo.expensio.dto.SubCategoryDto;
import com.cashigo.expensio.dto.exception.NoBudgetDefinitionFoundException;
import com.cashigo.expensio.dto.exception.NotValidRecurrenceException;
import com.cashigo.expensio.model.BudgetCycle;
import com.cashigo.expensio.model.BudgetDefinition;
import com.cashigo.expensio.repository.BudgetCycleRepository;
import com.cashigo.expensio.repository.BudgetDefinitionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetCycleService {

    @Value("${zone.id}")
    private String zone;

    private final BudgetDefinitionRepository budgetDefinitionRepository;
    private final SubCategoryService subCategoryService;


    public BudgetCycle getActiveBudgetCycleByBudgetDefinition(UUID budgetDefinitionId, String userId) {
        BudgetDefinition budgetDefinition = budgetDefinitionRepository
                .findBudgetDefinitionByIdWithCycles(budgetDefinitionId, userId)
                .orElseThrow(NoBudgetDefinitionFoundException::new);
        return budgetDefinition
                .getBudgetCycles()
                .stream()
                .filter(BudgetCycle::isActive)
                .findFirst()
                .orElse(null);
    }

    public BudgetCycle getActiveBudgetCycleByBudgetDefinition(BudgetDefinition budgetDefinition) {
        UUID budgetDefinitionId = budgetDefinition.getId();
        String userId = budgetDefinition.getUserId();
        return getActiveBudgetCycleByBudgetDefinition(budgetDefinitionId, userId);
    }

    public BudgetCycle getActiveBudgetCycleBySubCategoryId(Long subCategoryId, String userId) {
        SubCategoryDto subCategory = subCategoryService.getSubCategoryById(subCategoryId);
        Long categoryId = subCategory.getCategoryId();
        BudgetDefinition budgetDefinition = budgetDefinitionRepository
                .findBudgetDefinitionByCategoryWithCycles(categoryId, userId)
                .orElse(null);

        if (budgetDefinition == null) return null;

        return budgetDefinition
                .getBudgetCycles()
                .stream()
                .filter(BudgetCycle::isActive)
                .findFirst()
                .orElse(null);
    }

    public BudgetCycle getBudgetCycleByInstant(Long subCategoryId, Instant transactionInstant, String userId) {
        SubCategoryDto subCategory = subCategoryService.getSubCategoryById(subCategoryId);
        Long categoryId = subCategory.getCategoryId();
        BudgetDefinition budgetDefinition = budgetDefinitionRepository
                .findBudgetDefinitionByCategoryAndDateRangeWithCycles(categoryId, transactionInstant, userId)
                .orElse(null);

        if (budgetDefinition == null) return null;

        return budgetDefinition
                .getBudgetCycles()
                .stream()
                .filter(cycle ->
                        !transactionInstant.isBefore(cycle.getCycleStartDateTime()) &&
                                !transactionInstant.isAfter(cycle.getCycleEndDateTime())
                )
                .findFirst()
                .orElse(null);
    }

    public BudgetCycle createBudgetCycle(BudgetDefinition budgetDefinition) {

        LocalDate cycleStartDate, cycleEndDate;
        ZoneId zoneId = ZoneId.of(zone);
        LocalDate now = LocalDate.now(zoneId);

        if (budgetDefinition.getBudgetRecurrenceType().equals(BudgetRecurrence.WEEKLY)) {
            cycleStartDate = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            cycleEndDate = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        } else if (budgetDefinition.getBudgetRecurrenceType().equals(BudgetRecurrence.MONTHLY)) {
            cycleStartDate = now.with(TemporalAdjusters.firstDayOfMonth());
            cycleEndDate = now.with(TemporalAdjusters.lastDayOfMonth());
        } else
            throw new NotValidRecurrenceException();

        Instant cycleStartInstant = cycleStartDate.atStartOfDay(zoneId).toInstant();
        Instant cycleEndInstant = cycleEndDate.atTime(LocalTime.MAX).atZone(zoneId).toInstant().truncatedTo(ChronoUnit.SECONDS);

        BudgetCycle budgetCycle = new BudgetCycle();
        budgetCycle.setActive(true);
        budgetCycle.setBudgetDefinition(budgetDefinition);
        budgetCycle.setCycleStartDateTime(cycleStartInstant);
        budgetCycle.setCycleEndDateTime(cycleEndInstant);

        return budgetCycle;
    }
}
