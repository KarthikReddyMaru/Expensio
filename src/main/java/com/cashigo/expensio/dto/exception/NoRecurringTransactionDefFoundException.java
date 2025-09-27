package com.cashigo.expensio.dto.exception;

public class NoRecurringTransactionDefFoundException extends RuntimeException {
  public NoRecurringTransactionDefFoundException() {
    super("No recurring transaction definition found");
  }

  public NoRecurringTransactionDefFoundException(String message) {
        super(message);
    }
}
