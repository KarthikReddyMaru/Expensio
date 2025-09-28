package com.cashigo.expensio.dto;

import java.math.BigDecimal;

public interface ReportProjection {
    BigDecimal getAmountSpent();
    String getCategory();
}
