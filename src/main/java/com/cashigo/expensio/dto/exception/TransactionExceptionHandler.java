package com.cashigo.expensio.dto.exception;

import com.cashigo.expensio.dto.ErrorResponse;
import com.cashigo.expensio.dto.mapper.ErrorResponseMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TransactionExceptionHandler {

    @ExceptionHandler(NoTransactionFoundException.class)
    public ResponseEntity<ErrorResponse> noTransactionFound(NoTransactionFoundException exception) {
        ErrorResponse errorResponse = ErrorResponseMapper.fromException(exception, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
