package com.cashigo.expensio.dto.summary;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
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

    @CsvBindByName(column = "Date", required = true)
    @CsvDate(value = "dd-MM-yyyy", writeFormat = "dd-MM-yyyy")
    @NotNull(message = "Date cannot be null")
    private LocalDate transactionDate;

    @CsvBindByName(column = "Time")
    @CsvDate(value = "HH:mm:ss", writeFormat = "HH:mm:ss")
    private LocalTime transactionTime;

    @CsvBindByName(column = "Category")
    @NotNull
    private String category;

    @CsvBindByName(column = "SubCategory")
    @NotNull
    private String subCategory;

    @CsvBindByName(column = "Amount", required = true)
    @DecimalMin(value = "0", message = "Amount should be greater than 0")
    private BigDecimal amount;

    @CsvBindByName(column = "Note")
    private String note;

    public LocalTime getTransactionTime() {
        return this.transactionTime == null ? LocalTime.of(0, 0, 0) : this.transactionTime;
    }
}
