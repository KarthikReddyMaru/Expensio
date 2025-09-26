package com.cashigo.expensio.dto.summary;

import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class TransactionSummaryDto {
    private UUID id;
    private BigDecimal amount;
    private String category;
    private String subCategory;
    private ZonedDateTime transactionDateTime;
    private String note;
}
