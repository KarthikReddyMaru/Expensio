package com.cashigo.expensio.dto.exception;

public class NoTransactionFoundException extends RuntimeException {

    public NoTransactionFoundException(String message) {
        super(message);
    }

    public NoTransactionFoundException() {
        super("No transaction found");
    }
}
