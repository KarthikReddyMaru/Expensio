package com.cashigo.expensio.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CategoryReportDto {

    private String category;
    private BigDecimal amountSpent;
    private BigDecimal percentage;

}
