package com.cashigo.expensio.dto.exception;

import com.cashigo.expensio.dto.ErrorResponse;
import com.cashigo.expensio.dto.mapper.ErrorResponseMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> invalidMethodArgs(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        assert fieldError != null;
        String errorMessage = fieldError.getDefaultMessage();
        ErrorResponse errorResponse = ErrorResponseMapper.fromMessage(errorMessage, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
