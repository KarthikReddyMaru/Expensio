package com.cashigo.expensio.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ImportErrorDto {

    @CsvBindByName(column = "Date", required = true)
    private String transactionDate;

    @CsvBindByName(column = "Time")
    private String transactionTime;

    @CsvBindByName(column = "Category", required = true)
    private String category;

    @CsvBindByName(column = "SubCategory", required = true)
    private String subCategory;

    @CsvBindByName(column = "Amount", required = true)
    private String amount;

    @CsvBindByName(column = "Note")
    private String note;

    @CsvBindByName(column = "Reason")
    private String reason;
}

