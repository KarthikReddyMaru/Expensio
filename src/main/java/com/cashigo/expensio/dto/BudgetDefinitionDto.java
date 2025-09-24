package com.cashigo.expensio.dto;

import com.cashigo.expensio.common.consts.BudgetRecurrence;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class BudgetDefinitionDto {
    private UUID id;
    private Long categoryId;
    @DecimalMin(value = "0.0", message = "Budget cannot be less than 0")
    private BigDecimal budgetAmount;
    private BudgetRecurrence budgetRecurrenceType;
}

