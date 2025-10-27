package com.cashigo.expensio.dto.mapper;

import com.cashigo.expensio.dto.TransactionExportProjection;
import com.cashigo.expensio.dto.summary.TransactionSummaryDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
public class TransactionExportProjectionMapper implements Mapper<TransactionExportProjection, TransactionSummaryDto> {

    @Value("${zone.id}")
    private String zone;

    @Override
    public TransactionSummaryDto map(TransactionExportProjection projection) {
        return TransactionSummaryDto
                .builder()
                .category(projection.getCategory())
                .subCategory(projection.getSubCategory())
                .amount(projection.getAmount())
                .transactionTime(toLocalTime(projection.getTransactionDateTime()))
                .transactionDate(toLocalDate(projection.getTransactionDateTime()))
                .note(projection.getNote())
                .build();
    }

    public LocalTime toLocalTime(Instant instant) {
        return instant.atZone(ZoneId.of(zone)).toLocalTime().truncatedTo(ChronoUnit.SECONDS);
    }

    public LocalDate toLocalDate(Instant instant) {
        return instant.atZone(ZoneId.of(zone)).toLocalDate();
    }
}
