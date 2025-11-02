package com.cashigo.expensio.service;

import com.cashigo.expensio.common.consts.BudgetRecurrence;
import com.cashigo.expensio.dto.SubCategoryDto;
import com.cashigo.expensio.model.BudgetCycle;
import com.cashigo.expensio.model.BudgetDefinition;
import com.cashigo.expensio.repository.BudgetDefinitionRepository;
import com.cashigo.expensio.service.budget.BudgetCycleService;
import com.cashigo.expensio.service.category.SubCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BudgetCycleServiceTest {

    @Mock
    private BudgetDefinitionRepository budgetDefinitionRepository;
    @Mock
    private SubCategoryService subCategoryService;

    @InjectMocks
    private BudgetCycleService budgetCycleService;

    private String currentLoggedInUserId;

    @BeforeEach
    public void init() {
        budgetCycleService.setZone("Asia/Kolkata");
        currentLoggedInUserId = UUID.randomUUID().toString();
    }

    @Test
    void whenFetchingBudgetCycleByInstant_thenRespectiveBudgetCycleIsReturned() {
        Long subCategoryId = 3L, categoryId = 5L;
        Instant transactionDateTime = Instant.now();

        UUID prevCycleId = UUID.randomUUID();
        Instant prevCycleStart = Instant.now().minusSeconds(60 * 60 * 24 * 7);
        Instant prevCycleEnd = Instant.now().minusSeconds(1);
        BudgetCycle prevCycle = createBudgetCycle(prevCycleId, prevCycleStart, prevCycleEnd);

        UUID curCycleId = UUID.randomUUID();
        Instant curCycleEnd = Instant.now().plusSeconds(60 * 60 * 24 * 7);
        BudgetCycle curCycle = createBudgetCycle(curCycleId, transactionDateTime, curCycleEnd);

        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setCategoryId(categoryId);
        BudgetDefinition budgetDefinition = new BudgetDefinition();
        budgetDefinition.setBudgetCycles(List.of(prevCycle, curCycle));

        when(subCategoryService.getSubCategoryById(subCategoryId)).thenReturn(subCategoryDto);
        when(budgetDefinitionRepository
                .findBudgetDefinitionByCategoryAndDateRangeWithCycles(categoryId, transactionDateTime, currentLoggedInUserId))
                .thenReturn(Optional.of(budgetDefinition));

        BudgetCycle result = budgetCycleService
                .getBudgetCycleByInstant(subCategoryId, transactionDateTime, currentLoggedInUserId);

        assertThat(result).extracting(BudgetCycle::getId).isEqualTo(curCycleId);
    }

    @Test
    void whenFetchingBudgetCycleByFutureInstant_thenNoBudgetCycleIsReturned() {
        Long subCategoryId = 3L, categoryId = 5L;
        Instant transactionDateTime = Instant.now().plusSeconds(24 * 7 * 5 * 60 * 60);

        UUID prevCycleId = UUID.randomUUID();
        Instant prevCycleStart = Instant.now().minusSeconds(60 * 60 * 24 * 7);
        Instant prevCycleEnd = Instant.now().minusSeconds(1);
        BudgetCycle prevCycle = createBudgetCycle(prevCycleId, prevCycleStart, prevCycleEnd);

        UUID curCycleId = UUID.randomUUID();
        Instant curStartEnd = Instant.now();
        Instant curCycleEnd = Instant.now().plusSeconds(60 * 60 * 24 * 7);
        BudgetCycle curCycle = createBudgetCycle(curCycleId, curStartEnd, curCycleEnd);

        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setCategoryId(categoryId);
        BudgetDefinition budgetDefinition = new BudgetDefinition();
        budgetDefinition.setBudgetCycles(List.of(prevCycle, curCycle));

        when(subCategoryService.getSubCategoryById(subCategoryId)).thenReturn(subCategoryDto);
        when(budgetDefinitionRepository
                .findBudgetDefinitionByCategoryAndDateRangeWithCycles(categoryId, transactionDateTime, currentLoggedInUserId))
                .thenReturn(Optional.of(budgetDefinition));

        BudgetCycle result = budgetCycleService
                .getBudgetCycleByInstant(subCategoryId, transactionDateTime, currentLoggedInUserId);

        assertThat(result).isNull();
    }

    @ParameterizedTest
    @EnumSource(BudgetRecurrence.class)
    void whenCreatingBudgetCycle_thenBudgetCycleIsCreated(BudgetRecurrence budgetRecurrence) {
        BudgetDefinition def = new BudgetDefinition();
        def.setBudgetRecurrenceType(budgetRecurrence);

        BudgetCycle cycle = budgetCycleService.createBudgetCycle(def);

        ZoneId zone = ZoneId.of("Asia/Kolkata");
        LocalDate now = LocalDate.now(zone);
        LocalDate expectedStart;
        LocalDate expectedEnd;

        switch (budgetRecurrence) {
            case WEEKLY -> {
                expectedStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                expectedEnd = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            }
            case MONTHLY -> {
                expectedStart = now.with(TemporalAdjusters.firstDayOfMonth());
                expectedEnd = now.with(TemporalAdjusters.lastDayOfMonth());
            }
            default -> {
                return;
            }
        }

        assertThat(expectedStart.atStartOfDay(zone).toInstant())
                .isEqualTo(cycle.getCycleStartDateTime());
        assertThat(expectedEnd.atTime(LocalTime.MAX).atZone(zone).toInstant().truncatedTo(ChronoUnit.SECONDS))
                .isEqualTo(cycle.getCycleEndDateTime());
        assertThat(cycle.isActive()).isTrue();
    }

    private BudgetCycle createBudgetCycle(UUID prevCycleId,
                                          Instant prevCycleStart, Instant prevCycleEnd) {
        BudgetCycle budgetCycle = new BudgetCycle();
        budgetCycle.setId(prevCycleId);
        budgetCycle.setCycleStartDateTime(prevCycleStart);
        budgetCycle.setCycleEndDateTime(prevCycleEnd);
        return budgetCycle;
    }

}
