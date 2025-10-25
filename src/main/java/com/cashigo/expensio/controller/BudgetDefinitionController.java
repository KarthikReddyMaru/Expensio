package com.cashigo.expensio.controller;

import com.cashigo.expensio.common.documentation.StandardErrorResponses;
import com.cashigo.expensio.common.validation.OnCreate;
import com.cashigo.expensio.common.validation.OnUpdate;
import com.cashigo.expensio.dto.BudgetDefinitionDto;
import com.cashigo.expensio.dto.Response;
import com.cashigo.expensio.service.BudgetDefinitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/budgetdefinition")
@RequiredArgsConstructor
@RestController
@StandardErrorResponses
@Tag(name = "BudgetDefinition")
public class BudgetDefinitionController {

    private final BudgetDefinitionService budgetDefinitionService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get active budgets of user") @ApiResponse(responseCode = "200")
    public ResponseEntity<Response<List<BudgetDefinitionDto>>> getBudgetDefinitionsByUserId() {
        Response<List<BudgetDefinitionDto>> response = new Response<>();
        List<BudgetDefinitionDto> budgetDefinitions = budgetDefinitionService.getBudgetDefinitionsByUserId();
        response.setData(budgetDefinitions);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{budgetDefinitionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get budget definition of user by definition Id") @ApiResponse(responseCode = "200")
    public ResponseEntity<Response<BudgetDefinitionDto>> getBudgetDefinitionById(@PathVariable UUID budgetDefinitionId) {
        Response<BudgetDefinitionDto> response = new Response<>();
        BudgetDefinitionDto budgetDefinition = budgetDefinitionService.getBudgetDefinitionById(budgetDefinitionId);
        response.setData(budgetDefinition);
        return ResponseEntity.ok(response);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create budget definition") @ApiResponse(responseCode = "201")
    public ResponseEntity<Response<BudgetDefinitionDto>> saveBudgetDefinition(
            @Validated(OnCreate.class) @RequestBody BudgetDefinitionDto budgetDefinitionDto) {
        Response<BudgetDefinitionDto> response = new Response<>();
        BudgetDefinitionDto budgetDefinition = budgetDefinitionService.saveBudgetDefinition(budgetDefinitionDto);
        response.setData(budgetDefinition);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update budget definition") @ApiResponse(responseCode = "200")
    public ResponseEntity<Response<BudgetDefinitionDto>> updateBudgetDefinition(
            @Validated(OnUpdate.class) @RequestBody BudgetDefinitionDto budgetDefinitionDto) {
        Response<BudgetDefinitionDto> response = new Response<>();
        BudgetDefinitionDto budgetDefinition = budgetDefinitionService.updateBudgetDefinition(budgetDefinitionDto);
        response.setData(budgetDefinition);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{budgetDefinitionId}")
    @Operation(summary = "Delete budget definition by definition ID") @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteBudgetDefinition(@PathVariable UUID budgetDefinitionId) {
        budgetDefinitionService.deleteByBudgetDefinition(budgetDefinitionId);
        return ResponseEntity.noContent().build();
    }
}
