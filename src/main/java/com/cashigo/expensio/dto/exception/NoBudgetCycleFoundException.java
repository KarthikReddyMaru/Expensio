package com.cashigo.expensio.dto.exception;

public class NoBudgetCycleFoundException extends RuntimeException {
    public NoBudgetCycleFoundException() {
        super("No Budget Cycle Found");
    }
    public NoBudgetCycleFoundException(String message) {
        super(message);
    }
}
