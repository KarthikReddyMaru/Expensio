package com.cashigo.expensio.service.summary;

import com.cashigo.expensio.common.security.UserContext;
import com.cashigo.expensio.dto.exception.NoBudgetDefinitionFoundException;
import com.cashigo.expensio.dto.summary.BudgetDefinitionSummaryDto;
import com.cashigo.expensio.dto.summary.mapper.BudgetDefinitionToSummaryMapper;
import com.cashigo.expensio.model.BudgetDefinition;
import com.cashigo.expensio.repository.BudgetDefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BudgetDefinitionSummaryService {

    private final BudgetDefinitionRepository budgetDefinitionRepository;
    private final BudgetDefinitionToSummaryMapper budgetDefinitionToSummaryMapper;

    public List<BudgetDefinitionSummaryDto> getBudgetDefinitionSummaries() {
        List<BudgetDefinition> budgetDefinition = budgetDefinitionRepository.findBudgetDefinitionsWithCycles(UserContext.getUserId());
        return budgetDefinition
                .stream()
                .map(budgetDefinitionToSummaryMapper::map)
                .toList();
    }

    public BudgetDefinitionSummaryDto getBudgetDefinitionSummary(UUID budgetDefinitionId) {
        BudgetDefinition budgetDefinition = budgetDefinitionRepository
                .findBudgetDefinitionByIdAndUserId(budgetDefinitionId, UserContext.getUserId())
                .orElseThrow(NoBudgetDefinitionFoundException::new);
        return budgetDefinitionToSummaryMapper.map(budgetDefinition);
    }

}
