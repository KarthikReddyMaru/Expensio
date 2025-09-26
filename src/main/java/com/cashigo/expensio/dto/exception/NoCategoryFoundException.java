package com.cashigo.expensio.dto.exception;

public class NoCategoryFoundException extends Exception {

    public NoCategoryFoundException() {
        super("Category not found");
    }

    public NoCategoryFoundException(String message) {
        super(message);
    }

}
