package com.cashigo.expensio.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class ErrorResponse {

    private String message;
    private int statusCode;
    private Instant timestamp;

}
