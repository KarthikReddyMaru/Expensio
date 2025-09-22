package com.cashigo.expensio.controller;

import com.cashigo.expensio.dto.BudgetDefinitionDto;
import com.cashigo.expensio.dto.Response;
import com.cashigo.expensio.service.BudgetDefinitionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/budgetdefinition")
@RequiredArgsConstructor
@RestController
public class BudgetDefinitionController {

    private final BudgetDefinitionService budgetDefinitionService;

    @GetMapping(params = {"pageNum"})
    public ResponseEntity<Response<List<BudgetDefinitionDto>>> getBudgetDefinitionsByUserId(
            @RequestParam(required = false, defaultValue = "0") int pageNum) {
        Response<List<BudgetDefinitionDto>> response = new Response<>();
        List<BudgetDefinitionDto> budgetDefinitions = budgetDefinitionService.getBudgetDefinitionsByUserId(pageNum);
        response.setData(budgetDefinitions);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{budgetDefinitionId}")
    public ResponseEntity<Response<BudgetDefinitionDto>> getBudgetDefinitionById(@PathVariable UUID budgetDefinitionId) {
        Response<BudgetDefinitionDto> response = new Response<>();
        BudgetDefinitionDto budgetDefinition = budgetDefinitionService.getBudgetDefinitionById(budgetDefinitionId);
        response.setData(budgetDefinition);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Response<BudgetDefinitionDto>> saveBudgetDefinition(
            @Valid @RequestBody BudgetDefinitionDto budgetDefinitionDto) {
        Response<BudgetDefinitionDto> response = new Response<>();
        BudgetDefinitionDto budgetDefinition = budgetDefinitionService.saveOrUpdateBudgetDefinition(budgetDefinitionDto);
        response.setData(budgetDefinition);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Response<BudgetDefinitionDto>> updateBudgetDefinition(
            @Valid @RequestBody BudgetDefinitionDto budgetDefinitionDto) {
        Response<BudgetDefinitionDto> response = new Response<>();
        BudgetDefinitionDto budgetDefinition = budgetDefinitionService.saveOrUpdateBudgetDefinition(budgetDefinitionDto);
        response.setData(budgetDefinition);
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{budgetDefinitionId}")
    public ResponseEntity<Void> deleteBudgetDefinition(@PathVariable UUID budgetDefinitionId) {
        budgetDefinitionService.deleteByBudgetDefinition(budgetDefinitionId);
        return ResponseEntity.noContent().build();
    }
}
