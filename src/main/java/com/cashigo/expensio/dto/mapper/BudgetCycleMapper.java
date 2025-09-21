package com.cashigo.expensio.dto.mapper;

import com.cashigo.expensio.dto.BudgetCycleDto;
import com.cashigo.expensio.model.BudgetCycle;
import com.cashigo.expensio.model.BudgetDefinition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

@Component
public class BudgetCycleMapper implements BiMapper<BudgetCycleDto, BudgetCycle> {

    @Value("${zone.id}")
    private String zone;

    @Override
    public BudgetCycle mapToEntity(BudgetCycleDto dto) {
        if (dto == null) return null;

        BudgetCycle entity = new BudgetCycle();
        entity.setBudgetCycleId(dto.getBudgetCycleId());

        if (dto.getBudgetDefinitionId() != null) {
            BudgetDefinition budgetDef = new BudgetDefinition();
            budgetDef.setId(dto.getBudgetDefinitionId());
            entity.setBudgetDefinition(budgetDef);
        }

        entity.setAmountSpent(dto.getAmountSpent());
        entity.setActive(dto.isActive());

        return entity;
    }

    @Override
    public BudgetCycleDto mapToDto(BudgetCycle entity) {
        if (entity == null) return null;

        BudgetCycleDto dto = new BudgetCycleDto();
        dto.setBudgetCycleId(entity.getBudgetCycleId());
        dto.setBudgetDefinitionId(entity.getBudgetDefinition() != null ? entity.getBudgetDefinition().getId() : null);

        ZoneId zoneId = ZoneId.of(zone);
        LocalDate cycleStartDate = entity.getCycleStartDateTime().atZone(zoneId).toLocalDate();
        LocalDate cycleEndDate = entity.getCycleEndDateTime().atZone(zoneId).toLocalDate();
        dto.setCycleStartDateTime(cycleStartDate);
        dto.setCycleEndDateTime(cycleEndDate);

        dto.setAmountSpent(entity.getAmountSpent());
        dto.setActive(entity.isActive());

        return dto;
    }
}
