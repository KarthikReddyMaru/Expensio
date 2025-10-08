package com.cashigo.expensio.batch.exception;

import com.cashigo.expensio.dto.ErrorResponse;
import com.cashigo.expensio.dto.mapper.ErrorResponseMapper;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BatchExceptionHandler {

    @ExceptionHandler(JobInstanceAlreadyCompleteException.class)
    public ResponseEntity<ErrorResponse> jobInstanceAlreadyCompleteException(JobInstanceAlreadyCompleteException exception) {
        ErrorResponse errorResponse = ErrorResponseMapper.fromException(exception, HttpStatus.CONFLICT);
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
}
