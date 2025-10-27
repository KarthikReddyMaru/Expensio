package com.cashigo.expensio.service.summary;

import com.cashigo.expensio.common.security.UserContext;
import com.cashigo.expensio.dto.summary.RecurringTranDefSummaryDto;
import com.cashigo.expensio.dto.exception.NoRecurringTransactionDefFoundException;
import com.cashigo.expensio.dto.summary.mapper.RecurringTransactionDefToSummaryMapper;
import com.cashigo.expensio.model.RecurringTransactionDefinition;
import com.cashigo.expensio.repository.RecurringTransactionDefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecurringTransactionSummaryService {

    private final RecurringTransactionDefinitionRepository recurringTransactionDefinitionRepository;
    private final RecurringTransactionDefToSummaryMapper summaryMapper;

    public RecurringTranDefSummaryDto getRecurringTransactionById(UUID definitionId) {
        RecurringTransactionDefinition definition = recurringTransactionDefinitionRepository
                .findByIdAndUserId(definitionId, UserContext.getUserId())
                .orElseThrow(NoRecurringTransactionDefFoundException::new);
        return summaryMapper.map(definition);
    }

    public List<UUID> getRecurringTransactionsOfUser() {
        List<RecurringTransactionDefinition> definitions = recurringTransactionDefinitionRepository
                .findByUserId(UserContext.getUserId());
        return definitions.stream().map(RecurringTransactionDefinition::getId).toList();
    }

}
