package com.cashigo.expensio.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class BudgetCycleDto {
    private UUID budgetCycleId;
    private UUID budgetDefinitionId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate cycleStartDateTime; // LocalDate for readability
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate cycleEndDateTime;
    private boolean isActive;
}

