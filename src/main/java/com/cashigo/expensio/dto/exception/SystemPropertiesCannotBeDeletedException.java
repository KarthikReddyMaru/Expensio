package com.cashigo.expensio.dto.exception;

public class SystemPropertiesCannotBeDeletedException extends RuntimeException {
  public SystemPropertiesCannotBeDeletedException() {
    super("System properties cannot be deleted");
  }
  public SystemPropertiesCannotBeDeletedException(String message) {
        super(message);
    }
}
