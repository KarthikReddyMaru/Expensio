package com.cashigo.expensio.dto;

import com.cashigo.expensio.common.consts.TransactionRecurrence;
import com.cashigo.expensio.model.SubCategory;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class RecurringTransactionDefinitionDto {

    private UUID id;
    private BigDecimal amount;
    private SubCategory subCategory;
    private TransactionRecurrence transactionRecurrenceType;
    private Instant lastProcessedInstant;
    private LocalDate nextOccurrence;
    private String note;
}
