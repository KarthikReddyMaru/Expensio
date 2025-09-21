package com.cashigo.expensio.dto.exception;

import com.cashigo.expensio.dto.ErrorResponse;
import com.cashigo.expensio.dto.mapper.ErrorResponseMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BudgetDefinitionExceptionHandler {

    @ExceptionHandler(NoBudgetDefinitionFoundException.class)
    public ResponseEntity<ErrorResponse> noBudgetFound(NoBudgetDefinitionFoundException exception) {
        ErrorResponse errorResponse = ErrorResponseMapper.fromException(exception, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

}
