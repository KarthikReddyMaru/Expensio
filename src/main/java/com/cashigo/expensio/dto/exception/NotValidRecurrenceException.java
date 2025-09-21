package com.cashigo.expensio.dto.exception;

public class NotValidRecurrenceException extends RuntimeException {
    public NotValidRecurrenceException() {
        super("Not a valid recurrence");
    }

    public NotValidRecurrenceException(String message) {
        super(message);
    }
}
