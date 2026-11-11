package com.jkservice.exception;

public class DuplicateResourceException extends RuntimeException {
  public DuplicateResourceException(String message) {
    super(message);
  }
}
