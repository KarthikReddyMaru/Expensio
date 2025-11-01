package com.cashigo.expensio.dto.projection;

import java.math.BigDecimal;
import java.time.Instant;

public interface TransactionExportProjection {

    String getCategory();
    String getSubCategory();
    BigDecimal getAmount();
    Instant getTransactionDateTime();
    String getNote();

}
