package com.cashigo.expensio.dto;

import com.cashigo.expensio.common.consts.BudgetRecurrence;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class BudgetDefinitionDto {

    private UUID id;

    @Positive(message = "Invalid ID")
    @NotNull(message = "Invalid ID")
    private Long categoryId;

    @DecimalMin(value = "0.0", message = "Budget cannot be less than 0")
    private BigDecimal budgetAmount;

    @NotNull(message = "Recurrence cannot be null")
    private BudgetRecurrence budgetRecurrenceType;
}

