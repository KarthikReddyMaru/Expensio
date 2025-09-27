package com.cashigo.expensio.service;

import com.cashigo.expensio.common.security.UserContext;
import com.cashigo.expensio.dto.BudgetDefinitionDto;
import com.cashigo.expensio.dto.exception.NoBudgetDefinitionFoundException;
import com.cashigo.expensio.dto.exception.NoCategoryFoundException;
import com.cashigo.expensio.dto.mapper.BudgetDefinitionMapper;
import com.cashigo.expensio.model.BudgetCycle;
import com.cashigo.expensio.model.BudgetDefinition;
import com.cashigo.expensio.repository.BudgetDefinitionRepository;
import com.cashigo.expensio.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final BudgetCycleService budgetCycleService;
    private final BudgetDefinitionMapper budgetDefinitionMapper;
    private final BudgetTrackingService budgetTrackingService;
    private final CategoryRepository categoryRepository;
    private final UserContext userContext;

    public BudgetDefinitionDto getBudgetDefinitionById(UUID budgetDefinitionId) {
        String userId = userContext.getUserId();
        Optional<BudgetDefinition> budgetDefinition = budgetDefinitionRepository
                .findBudgetDefinitionByIdAndUserId(budgetDefinitionId, userId);
        BudgetDefinition data = budgetDefinition.orElseThrow(NoBudgetDefinitionFoundException::new);
        return budgetDefinitionMapper.mapToDto(data);
    }

    public List<BudgetDefinitionDto> getBudgetDefinitionsByUserId() {
        String userId = userContext.getUserId();
        List<BudgetDefinition> budgetDefinitions = budgetDefinitionRepository.findBudgetDefinitionsByUserId(userId);
        return budgetDefinitions
                .stream().map(budgetDefinitionMapper::mapToDto).toList();
    }

    @SneakyThrows
    public BudgetDefinitionDto saveBudgetDefinition(BudgetDefinitionDto unsavedBudgetDefinition) {
        String userId = userContext.getUserId();
        BudgetDefinition budgetDefinition = budgetDefinitionMapper.mapToEntity(unsavedBudgetDefinition);
        budgetDefinition.setUserId(userId);

        Long categoryId = budgetDefinition.getCategory().getId();
        boolean categoryExists = categoryRepository.existsCategoryById(categoryId, userId);
        if (!categoryExists)
            throw new NoCategoryFoundException();

        BudgetCycle budgetCycle = budgetCycleService.createBudgetCycle(budgetDefinition);
        budgetDefinition.setBudgetCycles(new ArrayList<>(List.of(budgetCycle)));

        budgetDefinitionRepository.save(budgetDefinition);

        budgetTrackingService.addPreviousTransactionsToCurrentBudgetCycle(budgetCycle, categoryId);

        return budgetDefinitionMapper.mapToDto(budgetDefinition);
    }

    public BudgetDefinitionDto updateBudgetDefinition(BudgetDefinitionDto budgetDefinitionDto) {
        String userId = userContext.getUserId();
        UUID budgetDefinitionId = budgetDefinitionDto.getId();
        BudgetDefinition budgetDefinition = budgetDefinitionRepository
                .findBudgetDefinitionByIdAndUserId(budgetDefinitionId, userId)
                .orElseThrow(NoBudgetDefinitionFoundException::new);
        budgetDefinition.setUserId(userId);
        budgetDefinition.setBudgetAmount(budgetDefinitionDto.getBudgetAmount());
        BudgetDefinition saved = budgetDefinitionRepository.save(budgetDefinition);
        return budgetDefinitionMapper.mapToDto(saved);
    }

    @Transactional
    public void deleteByBudgetDefinition(UUID budgetDefinitionId) {
        String userId = userContext.getUserId();
        budgetDefinitionRepository.deleteBudgetDefinitionByIdAndUserId(budgetDefinitionId, userId);
        log.info("Budget definition of {} with id {}", userContext.getUserName(), budgetDefinitionId);
    }

}
