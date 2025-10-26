package com.cashigo.expensio.dto.summary;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
public class TransactionSummaryDto {

    @CsvBindByName(column = "Date")
    private LocalDateTime transactionDateTime;

    @CsvBindByName(column = "Category")
    private String category;

    @CsvBindByName(column = "SubCategory")
    private String subCategory;

    @CsvBindByName(column = "Amount")
    private BigDecimal amount;

    @CsvBindByName(column = "Note")
    private String note;
}
