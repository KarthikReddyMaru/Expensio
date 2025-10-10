package com.cashigo.expensio.dto.exception;

public class NoSubCategoryFoundException extends RuntimeException {

    public NoSubCategoryFoundException(String message) {
        super(message);
    }

    public NoSubCategoryFoundException() {
        super("No sub category found");
    }
}
