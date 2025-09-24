package com.cashigo.expensio.dto;

import com.cashigo.expensio.common.consts.TransactionRecurrence;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private UUID id;
    private String userId;
    private BigDecimal amount;
    private Long subCategoryId;
    private Instant transactionDateTime;
    private String note;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private TransactionRecurrence transactionRecurrenceType;
}
