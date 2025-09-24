package com.cashigo.expensio.dto.mapper;

import com.cashigo.expensio.dto.RecurringTransactionDefinitionDto;
import com.cashigo.expensio.model.RecurringTransactionDefinition;
import org.springframework.stereotype.Component;

@Component
public class RecurringTransactionDefinitionMapper
        implements Mapper<RecurringTransactionDefinition, RecurringTransactionDefinitionDto> {

    @Override
    public RecurringTransactionDefinitionDto map(RecurringTransactionDefinition entity) {
        return RecurringTransactionDefinitionDto.builder()
                .id(entity.getId())
                .amount(entity.getAmount())
                .subCategory(entity.getSubCategory())
                .transactionRecurrenceType(entity.getTransactionRecurrenceType())
                .lastProcessedInstant(entity.getLastProcessedInstant())
                .nextOccurrence(entity.getNextOccurrence())
                .note(entity.getNote())
                .build();
    }
}
