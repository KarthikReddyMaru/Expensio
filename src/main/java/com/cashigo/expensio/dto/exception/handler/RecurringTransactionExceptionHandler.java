package com.cashigo.expensio.dto.exception.handler;

import com.cashigo.expensio.dto.ErrorResponse;
import com.cashigo.expensio.dto.exception.InvalidRecurrenceTransactionException;
import com.cashigo.expensio.dto.exception.NoRecurringTransactionDefFoundException;
import com.cashigo.expensio.dto.mapper.ErrorResponseMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RecurringTransactionExceptionHandler {

    @ExceptionHandler(InvalidRecurrenceTransactionException.class)
    public ResponseEntity<ErrorResponse> invalidRecurrenceTransaction(InvalidRecurrenceTransactionException exception) {
        ErrorResponse errorResponse = ErrorResponseMapper.fromException(exception, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoRecurringTransactionDefFoundException.class)
    public ResponseEntity<ErrorResponse> noRecurringTranDef(NoRecurringTransactionDefFoundException exception) {
        ErrorResponse errorResponse = ErrorResponseMapper.fromException(exception, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

}
