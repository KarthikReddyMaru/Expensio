package com.cashigo.expensio.dto;

import com.cashigo.expensio.common.consts.TransactionRecurrence;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private UUID id;
    @DecimalMin(value = "0.0", message = "Amount cannot be less than 0")
    private BigDecimal amount;
    private Long subCategoryId;
    @Schema(example = "2025-10-02T23:59:59Z")
    private LocalDateTime transactionDateTime;
    private String note;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private TransactionRecurrence transactionRecurrenceType;
}
