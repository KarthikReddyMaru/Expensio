package com.cashigo.expensio.dto.exception;

public class SystemPropertiesCannotBeModifiedException extends RuntimeException {
  public SystemPropertiesCannotBeModifiedException() {
    super("System properties cannot be modified");
  }
  public SystemPropertiesCannotBeModifiedException(String message) {
        super(message);
    }
}
