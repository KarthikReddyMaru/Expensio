package com.cashigo.expensio.dto.summary.mapper;

import com.cashigo.expensio.dto.mapper.Mapper;
import com.cashigo.expensio.dto.summary.RecurringTranDefSummaryDto;
import com.cashigo.expensio.model.RecurringTransactionDefinition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class RecurringTransactionDefToSummaryMapper implements Mapper<RecurringTransactionDefinition, RecurringTranDefSummaryDto> {

    @Value("${zone.id}")
    private String zone;

    @Override
    public RecurringTranDefSummaryDto map(RecurringTransactionDefinition entity) {
        return RecurringTranDefSummaryDto
                .builder()
                .id(entity.getId())
                .amount(entity.getAmount())
                .category(entity.getSubCategory().getCategory().getName())
                .subCategory(entity.getSubCategory().getName())
                .lastProcessedInstant(mapToLocalDateTime(entity.getLastProcessedInstant()))
                .nextOccurrence(entity.getNextOccurrence())
                .recurrence(entity.getTransactionRecurrenceType())
                .note(entity.getNote())
                .build();
    }

    private LocalDateTime mapToLocalDateTime(Instant instant) {
        ZoneId zoneId = ZoneId.of(zone);
        return instant.atZone(zoneId).toLocalDateTime();
    }
}
