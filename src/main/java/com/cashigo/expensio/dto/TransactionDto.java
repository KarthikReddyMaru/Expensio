package com.cashigo.expensio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto implements Dto {
    private UUID id;
    private String userId;
    private BigDecimal amount;
    private Long subCategoryId;
    private Instant transactionDateTime;
    private String note;
}
