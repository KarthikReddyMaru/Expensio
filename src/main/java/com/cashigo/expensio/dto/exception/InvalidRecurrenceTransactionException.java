package com.cashigo.expensio.dto.exception;

public class InvalidRecurrenceTransactionException extends RuntimeException {

    public InvalidRecurrenceTransactionException(String message) {
        super(message);
    }
}
