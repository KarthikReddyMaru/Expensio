package com.cashigo.expensio.dto.projection;

import java.math.BigDecimal;

public interface ReportProjection {
    BigDecimal getAmountSpent();
    String getCategory();
}
