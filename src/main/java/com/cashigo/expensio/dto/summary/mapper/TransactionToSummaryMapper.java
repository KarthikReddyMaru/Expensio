package com.cashigo.expensio.dto.summary.mapper;

import com.cashigo.expensio.dto.mapper.Mapper;
import com.cashigo.expensio.dto.summary.TransactionSummaryDto;
import com.cashigo.expensio.model.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.*;

@Component
public class TransactionToSummaryMapper implements Mapper<Transaction, TransactionSummaryDto> {

    @Value("${zone.id}")
    private String zone;

    @Override
    public TransactionSummaryDto map(Transaction entity) {

        return TransactionSummaryDto.builder()
                .category(getCategory(entity))
                .subCategory(getSubCategory(entity))
                .amount(entity.getAmount())
                .transactionTime(toLocalTime(entity.getTransactionDateTime()))
                .transactionDate(toLocalDate(entity.getTransactionDateTime()))
                .note(entity.getNote())
                .build();

    }

    private String getSubCategory(Transaction entity) {
        if (entity.getSubCategory() != null)
            return entity.getSubCategory().getName();
        return null;
    }

    private String getCategory(Transaction entity) {
        if (entity.getSubCategory() != null && entity.getSubCategory().getCategory() != null)
            return entity.getSubCategory().getCategory().getName();
        return null;
    }

    public LocalTime toLocalTime(Instant instant) {
        instant = instant.atZone(ZoneId.of("UTC")).toInstant();
        return instant.atZone(ZoneId.of(zone)).toLocalTime();
    }

    public LocalDate toLocalDate(Instant instant) {
        instant = instant.atZone(ZoneId.of("UTC")).toInstant();
        return instant.atZone(ZoneId.of(zone)).toLocalDate();
    }

}
