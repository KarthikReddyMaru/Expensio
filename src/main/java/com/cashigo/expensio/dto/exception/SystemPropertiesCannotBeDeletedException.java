package com.cashigo.expensio.dto.exception;

public class SystemPropertiesCannotBeDeletedException extends RuntimeException {
  public SystemPropertiesCannotBeDeletedException(String message) {
    super(message);
  }
}
