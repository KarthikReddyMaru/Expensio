package com.cashigo.expensio.service;

import com.cashigo.expensio.common.consts.Recurrence;
import com.cashigo.expensio.common.security.UserContext;
import com.cashigo.expensio.dto.BudgetDefinitionDto;
import com.cashigo.expensio.dto.exception.NoBudgetDefinitionFoundException;
import com.cashigo.expensio.dto.exception.NotValidRecurrenceException;
import com.cashigo.expensio.dto.mapper.BudgetDefinitionMapper;
import com.cashigo.expensio.model.BudgetCycle;
import com.cashigo.expensio.model.BudgetDefinition;
import com.cashigo.expensio.repository.BudgetDefinitionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetDefinitionService {

    @Value("${page.size}")
    private int pageSize;

    @Value("${zone.id}")
    private String zone;

    private final BudgetDefinitionRepository budgetDefinitionRepository;
    private final UserContext userContext;
    private final BudgetDefinitionMapper budgetDefinitionMapper;

    public BudgetDefinitionDto getBudgetDefinitionById(UUID budgetDefinitionId) {
        String userId = userContext.getUserId();
        Optional<BudgetDefinition> budgetDefinition = budgetDefinitionRepository
                .findBudgetDefinitionByIdAndUserId(budgetDefinitionId, userId);
        BudgetDefinition data = budgetDefinition.orElseThrow(NoBudgetDefinitionFoundException::new);
        return budgetDefinitionMapper.mapToDto(data);
    }

    public List<BudgetDefinitionDto> getBudgetDefinitionsByUserId(int pageNum) {
        String userId = userContext.getUserId();
        Sort sortByCategory = Sort.by("category").ascending();
        Pageable pageable = PageRequest.of(pageNum, pageSize, sortByCategory);
        List<BudgetDefinition> budgetDefinitions = budgetDefinitionRepository.findBudgetDefinitionsByUserId(userId, pageable);
        return budgetDefinitions
                .stream().map(budgetDefinitionMapper::mapToDto).toList();
    }

    public BudgetDefinition getBudgetDefinitionByCategoryAndUserId(Long categoryId) {
        String userId = userContext.getUserId();
        Optional<BudgetDefinition> budgetDefinition =
                budgetDefinitionRepository.findBudgetDefinitionByCategory_IdAndUserId(categoryId, userId);
        return budgetDefinition.orElse(null);
    }

    @Transactional
    public BudgetDefinitionDto saveOrUpdateBudgetDefinition(BudgetDefinitionDto unsavedBudgetDefinition) {
        String userId = userContext.getUserId();
        BudgetDefinition budgetDefinition;
        if (unsavedBudgetDefinition.getId() != null) {
            UUID savedBudgetDefinitionId = unsavedBudgetDefinition.getId();
            budgetDefinition = budgetDefinitionRepository
                    .findById(savedBudgetDefinitionId)
                    .orElseThrow(NoBudgetDefinitionFoundException::new);
            budgetDefinition = budgetDefinitionMapper.mapToEntity(unsavedBudgetDefinition, budgetDefinition);
        } else
            budgetDefinition = budgetDefinitionMapper.mapToEntity(unsavedBudgetDefinition);
        budgetDefinition.setUserId(userId);
        ZoneId zoneId = ZoneId.of(zone);
        LocalDate now = LocalDate.now(zoneId);
        BudgetCycle newBudgetCycle;
        if (budgetDefinition.getId() == null) {
            if (budgetDefinition.getRecurrenceType().equals(Recurrence.WEEKLY)) {
                LocalDate cycleStartDate = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                LocalDate cycleEndDate = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                newBudgetCycle = createCycle(cycleStartDate, zoneId, cycleEndDate, budgetDefinition);
            } else if (budgetDefinition.getRecurrenceType().equals(Recurrence.MONTHLY)) {
                LocalDate cycleStartDate = now.with(TemporalAdjusters.firstDayOfMonth());
                LocalDate cycleEndDate = now.with(TemporalAdjusters.lastDayOfMonth());
                newBudgetCycle = createCycle(cycleStartDate, zoneId, cycleEndDate, budgetDefinition);
            } else
                throw new NotValidRecurrenceException();
            budgetDefinition.setBudgetCycles(new ArrayList<>(List.of(newBudgetCycle)));
        }
        BudgetDefinition savedBudgetDefinition = budgetDefinitionRepository.save(budgetDefinition);
        return budgetDefinitionMapper.mapToDto(savedBudgetDefinition);
    }

    @Transactional
    public void deleteByBudgetDefinition(UUID budgetDefinitionId) {
        String userId = userContext.getUserId();
        budgetDefinitionRepository.deleteBudgetDefinitionByIdAndUserId(budgetDefinitionId, userId);
        log.info("Budget definition of {} with id {}", userContext.getUserName(), budgetDefinitionId);
    }

    public BudgetCycle createCycle(LocalDate cycleStartDate, ZoneId zoneId, LocalDate cycleEndDate, BudgetDefinition budgetDefinition) {
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
