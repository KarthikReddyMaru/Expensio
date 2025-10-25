package com.cashigo.expensio.dto;

import com.cashigo.expensio.common.consts.BudgetRecurrence;
import com.cashigo.expensio.common.validation.OnCreate;
import com.cashigo.expensio.common.validation.OnUpdate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class BudgetDefinitionDto {

    @Null(groups = {OnCreate.class}, message = "Id should be null")
    @NotNull(groups = {OnUpdate.class}, message = "Id cannot be null")
    private UUID id;

    @Positive(message = "Invalid Category Id")
    @NotNull(message = "Invalid Category Id", groups = {OnCreate.class})
    @Null(message = "CategoryId cannot be updated", groups = {OnUpdate.class})
    @Schema(example = "3")
    private Long categoryId;

    @DecimalMin(value = "0.0", message = "Budget cannot be less than 0", groups = {OnCreate.class, OnUpdate.class})
    private BigDecimal budgetAmount;

    @NotNull(message = "Recurrence cannot be null", groups = {OnCreate.class})
    @Null(message = "Recurrence cannot be updated", groups = {OnUpdate.class})
    private BudgetRecurrence budgetRecurrenceType;
}

