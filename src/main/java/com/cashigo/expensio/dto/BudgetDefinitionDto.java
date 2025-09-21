package com.cashigo.expensio.dto;

import com.cashigo.expensio.common.consts.Recurrence;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class BudgetDefinitionDto implements Dto {
    private UUID id;
    private String userId;
    private Long categoryId;
    private BigDecimal budgetAmount;
    private Recurrence recurrenceType;
}

