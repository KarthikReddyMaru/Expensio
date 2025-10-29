package com.cashigo.expensio.dto.exception.handler;

import com.cashigo.expensio.dto.ErrorResponse;
import com.cashigo.expensio.dto.mapper.ErrorResponseMapper;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CSVExceptionHandler {

    @ExceptionHandler(CsvRequiredFieldEmptyException.class)
    public ResponseEntity<ErrorResponse> requiredFieldMissingException(CsvRequiredFieldEmptyException exception) {
        ErrorResponse errorResponse = ErrorResponseMapper.fromException(exception, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
