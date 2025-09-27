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
    private final UserContext userContext;

    public RecurringTranDefSummaryDto getRecurringTransactionById(UUID definitionId) {
        String userId = userContext.getUserId();
        RecurringTransactionDefinition definition = recurringTransactionDefinitionRepository
                .findByIdAndUserId(definitionId, userId)
                .orElseThrow(NoRecurringTransactionDefFoundException::new);
        return summaryMapper.map(definition);
    }

    public List<UUID> getRecurringTransactionsOfUser() {
        String userId = userContext.getUserId();
        List<RecurringTransactionDefinition> definitions = recurringTransactionDefinitionRepository
                .findByUserId(userId);
        return definitions.stream().map(RecurringTransactionDefinition::getId).toList();
    }

}
