package com.cashigo.expensio.dto.projection;

import java.time.Instant;

public interface BudgetDefCacheProjection {
    byte[] getBudgetCycleId();
    long getCategoryId();
    Instant getStart();
    Instant getEnd();
}

