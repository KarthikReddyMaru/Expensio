package com.cashigo.expensio.dto.mapper;

import com.cashigo.expensio.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ErrorResponseMapper {

    public static ErrorResponse fromException(Exception ex, HttpStatus status) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setStatusCode(status.value());
        errorResponse.setTimestamp(Instant.now());
        return errorResponse;
    }
}
