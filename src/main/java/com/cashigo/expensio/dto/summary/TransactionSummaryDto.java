package com.cashigo.expensio.dto.summary;

import com.opencsv.bean.CsvBindByName;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class TransactionSummaryDto {

    @CsvBindByName(column = "Date")
    private LocalDate transactionDate;

    @CsvBindByName(column = "Time")
    private LocalTime transactionTime;

    @CsvBindByName(column = "Category")
    private String category;

    @CsvBindByName(column = "SubCategory")
    private String subCategory;

    @CsvBindByName(column = "Amount")
    private BigDecimal amount;

    @CsvBindByName(column = "Note")
    private String note;
}
