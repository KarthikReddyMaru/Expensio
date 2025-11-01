package com.cashigo.expensio.controller.summary;

import com.cashigo.expensio.common.documentation.StandardErrorResponses;
import com.cashigo.expensio.dto.summary.RecurringTranDefSummaryDto;
import com.cashigo.expensio.dto.Response;
import com.cashigo.expensio.service.summary.RecurringTransactionSummaryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/summary/recurringtransaction")
@RequiredArgsConstructor
@StandardErrorResponses
@Tag(name = "Recurring Transactions - Summary")
public class RecurringTranDefSummaryController {

    private final RecurringTransactionSummaryService summaryService;

    @GetMapping
    public ResponseEntity<Response<List<UUID>>> getDefinitionsOfUser() {
        Response<List<UUID>> response = new Response<>();
        List<UUID> data = summaryService.getRecurringTransactionsOfUser();
        response.setData(data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{definitionId}")
    public ResponseEntity<Response<RecurringTranDefSummaryDto>> getDefinitionById(@PathVariable UUID definitionId) {
        Response<RecurringTranDefSummaryDto> response = new Response<>();
        RecurringTranDefSummaryDto data = summaryService.getRecurringTransactionById(definitionId);
        response.setData(data);
        return ResponseEntity.ok(response);
    }

}
