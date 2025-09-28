package com.cashigo.expensio.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ReportDto {

    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<CategoryReportDto> categoryReports;
    private BigDecimal totalAmountSpent;
}
