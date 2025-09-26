package com.cashigo.expensio.dto.exception.handler;

import com.cashigo.expensio.dto.ErrorResponse;
import com.cashigo.expensio.dto.exception.NoBudgetCycleFoundException;
import com.cashigo.expensio.dto.mapper.ErrorResponseMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BudgetCycleExceptionHandler {

    @ExceptionHandler(NoBudgetCycleFoundException.class)
    public ResponseEntity<ErrorResponse> noBudgetCycleFound(NoBudgetCycleFoundException exception) {
        ErrorResponse errorResponse = ErrorResponseMapper.fromException(exception, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

}
