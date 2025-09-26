package com.cashigo.expensio.dto.summary;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BudgetCycleSummaryDto {

    private UUID id;
    private UUID budgetDefinitionId;
    private LocalDateTime cycleStartDate;
    private LocalDateTime cycleEndDate;
    private BigDecimal amountSpent;

}
