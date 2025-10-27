package com.cashigo.expensio.dto.summary;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionSummaryDto {

    @CsvBindByName(column = "Date")
    @CsvDate(value = "dd-MM-yyyy", writeFormat = "dd-MM-yyyy")
    private LocalDate transactionDate;

    @CsvBindByName(column = "Time")
    @CsvDate(value = "HH:mm:ss", writeFormat = "HH:mm:ss")
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
