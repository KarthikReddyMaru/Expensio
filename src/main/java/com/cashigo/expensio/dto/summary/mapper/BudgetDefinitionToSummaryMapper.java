package com.cashigo.expensio.dto.summary.mapper;

import com.cashigo.expensio.dto.mapper.Mapper;
import com.cashigo.expensio.dto.summary.BudgetDefinitionSummaryDto;
import com.cashigo.expensio.model.BudgetCycle;
import com.cashigo.expensio.model.BudgetDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BudgetDefinitionToSummaryMapper implements Mapper<BudgetDefinition, BudgetDefinitionSummaryDto> {

    @Override
    public BudgetDefinitionSummaryDto map(BudgetDefinition entity) {
        BudgetDefinitionSummaryDto dto = new BudgetDefinitionSummaryDto();

        dto.setId(entity.getId());
        dto.setCategory(entity.getCategory().getName());
        dto.setBudgetAllocated(entity.getBudgetAmount());
        dto.setBudgetRecurrenceType(entity.getBudgetRecurrenceType().name());

        if (entity.getBudgetCycles() != null) {
            dto.setBudgetCycles(
                    entity.getBudgetCycles()
                            .stream()
                            .map(BudgetCycle::getId)
                            .toList()
            );
        }

        return dto;
    }
}
