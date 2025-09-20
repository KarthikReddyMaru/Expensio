package com.cashigo.expensio.dto.exception;

public class CategoryNotFoundException extends Exception {

    public CategoryNotFoundException() {
        super("Category not found");
    }

    public CategoryNotFoundException(String message) {
        super(message);
    }

}
