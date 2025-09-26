package com.cashigo.expensio.controller.summary;

import com.cashigo.expensio.dto.Response;
import com.cashigo.expensio.dto.summary.BudgetDefinitionSummaryDto;
import com.cashigo.expensio.service.summary.BudgetDefinitionSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/budgetdefinition/summary")
@RequiredArgsConstructor
public class BudgetDefinitionSummaryController {

    private final BudgetDefinitionSummaryService budgetDefinitionSummaryService;

    @GetMapping
    public ResponseEntity<Response<List<BudgetDefinitionSummaryDto>>> getBudgetDefinitionSummaries() {
        Response<List<BudgetDefinitionSummaryDto>> response = new Response<>();
        List<BudgetDefinitionSummaryDto> budgetDefinitionSummaries = budgetDefinitionSummaryService
                .getBudgetDefinitionSummaries();
        response.setData(budgetDefinitionSummaries);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{budgetDefinitionId}")
    public ResponseEntity<Response<BudgetDefinitionSummaryDto>> getBudgetDefinitionSummary(@PathVariable UUID budgetDefinitionId) {
        Response<BudgetDefinitionSummaryDto> response = new Response<>();
        BudgetDefinitionSummaryDto budgetDefinitionSummary = budgetDefinitionSummaryService
                .getBudgetDefinitionSummary(budgetDefinitionId);
        response.setData(budgetDefinitionSummary);
        return ResponseEntity.ok(response);
    }

}
