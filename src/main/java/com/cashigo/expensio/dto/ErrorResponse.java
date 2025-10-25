package com.cashigo.expensio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;

@Data
public class ErrorResponse {

    private String message;
    @Schema(example = "4XX")
    private int statusCode;
    private Instant timestamp;

}
