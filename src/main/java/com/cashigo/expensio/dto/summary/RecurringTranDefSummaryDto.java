package com.cashigo.expensio.dto.summary;

import com.cashigo.expensio.common.consts.TransactionRecurrence;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class RecurringTranDefSummaryDto {
    private UUID id;
    private BigDecimal amount;
    private String category;
    private String subCategory;
    private TransactionRecurrence recurrence;
    private LocalDateTime lastProcessedInstant;
    private LocalDate nextOccurrence;
    private String note;
}
