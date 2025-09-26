package com.cashigo.expensio.dto.summary.mapper;

import com.cashigo.expensio.dto.mapper.Mapper;
import com.cashigo.expensio.dto.summary.BudgetCycleSummaryDto;
import com.cashigo.expensio.model.BudgetCycle;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class BudgetCycleToSummaryMapper implements Mapper<BudgetCycle, BudgetCycleSummaryDto> {

    @Value("${zone.id}")
    private String zone;

    @Override
    public BudgetCycleSummaryDto map(BudgetCycle entity) {
        BudgetCycleSummaryDto dto = new BudgetCycleSummaryDto();

        dto.setId(entity.getId());
        dto.setBudgetDefinitionId(entity.getBudgetDefinition().getId());

        ZoneId zoneId = ZoneId.of(zone);

        Instant cycleStartDate = entity.getCycleStartDateTime();
        LocalDateTime localStartDate = cycleStartDate.atZone(zoneId).toLocalDateTime();

        dto.setCycleStartDate(localStartDate);

        Instant cycleEndDate = entity.getCycleEndDateTime();
        LocalDateTime localEndDate = cycleEndDate.atZone(zoneId).toLocalDateTime();

        dto.setCycleEndDate(localEndDate);

        return dto;
    }
}
