package com.cashigo.expensio.batch.refresh.transactions.model;

import com.cashigo.expensio.common.consts.TransactionRecurrence;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record RecurrencingTranDefRecord(UUID id, Instant lastProcessedInstant, LocalDate nextOccurrence, TransactionRecurrence transactionRecurrence) {
}
