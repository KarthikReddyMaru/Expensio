package com.cashigo.expensio.batch.refresh.budget.model;

import java.time.Instant;
import java.util.UUID;

public record BudgetRefreshRecord(UUID budgetDefId, UUID budgetCycleId, Instant start, Instant end, String userId) {}
