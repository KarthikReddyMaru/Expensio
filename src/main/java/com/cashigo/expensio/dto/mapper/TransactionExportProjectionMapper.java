package com.cashigo.expensio.dto.mapper;

import com.cashigo.expensio.dto.TransactionExportProjection;
import com.cashigo.expensio.dto.summary.TransactionSummaryDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

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
                .transactionDateTime(toLocalDateTime(projection.getTransactionDateTime()))
                .note(projection.getNote())
                .build();
    }

    public LocalDateTime toLocalDateTime(Instant instant) {
        return instant.atZone(ZoneId.of(zone)).toLocalDateTime().truncatedTo(ChronoUnit.SECONDS);
    }
}
