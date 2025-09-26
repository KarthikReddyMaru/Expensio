package com.cashigo.expensio.service;

import com.cashigo.expensio.common.security.UserContext;
import com.cashigo.expensio.dto.SubCategoryDto;
import com.cashigo.expensio.model.*;
import com.cashigo.expensio.repository.BudgetCycleRepository;
import com.cashigo.expensio.repository.BudgetDefinitionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetCycleService {

    private final BudgetCycleRepository budgetCycleRepository;
    private final BudgetDefinitionRepository budgetDefinitionRepository;
    private final SubCategoryService subCategoryService;
    private final UserContext userContext;

    public BudgetCycle getActiveBudgetCycleByBudgetDefinition(UUID budgetDefinitionId) {
        return budgetCycleRepository.findActiveBudgetCycleByBudgetDefinition_Id(budgetDefinitionId);
    }

    public BudgetCycle getActiveBudgetCycleBySubCategoryId(Long subCategoryId, String userId) {
        SubCategoryDto subCategory = subCategoryService.getSubCategoryById(subCategoryId);
        Long categoryId = subCategory.getCategoryId();
        log.info("Category to find Active Cycle {}", categoryId);
        BudgetDefinition budgetDefinition =
                budgetDefinitionRepository.findBudgetDefinitionByCategory_IdAndUserId(categoryId, userId).orElse(null);
        if (budgetDefinition != null) {
            UUID budgetDefinitionId = budgetDefinition.getId();
            log.info("Budget Def ID {}", budgetDefinitionId);
            return budgetCycleRepository
                    .findActiveBudgetCycleByBudgetDefinition_Id(budgetDefinitionId);
        }
        return null;
    }

}
