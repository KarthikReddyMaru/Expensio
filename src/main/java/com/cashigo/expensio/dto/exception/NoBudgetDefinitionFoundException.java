package com.cashigo.expensio.dto.exception;

public class NoBudgetDefinitionFoundException extends RuntimeException {
    public NoBudgetDefinitionFoundException() {
        super("No Budget Found");
    }
    public NoBudgetDefinitionFoundException(String message) {
        super(message);
    }
}
