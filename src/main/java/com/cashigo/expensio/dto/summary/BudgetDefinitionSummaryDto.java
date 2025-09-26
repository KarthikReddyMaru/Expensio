package com.cashigo.expensio.dto.summary;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class BudgetDefinitionSummaryDto {

    private UUID id;
    private String category;
    private BigDecimal budgetAllocated;
    private String budgetRecurrenceType;
    private List<UUID> budgetCycles;
    private UUID activeCycle;
}
