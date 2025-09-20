package com.cashigo.expensio.dto.exception;

public class NoSubCategoryFoundException extends Exception {

    public NoSubCategoryFoundException(String message) {
        super(message);
    }

    public NoSubCategoryFoundException() {
        super();
    }
}
