package com.jkservice.exception;

public class BusinessException extends RuntimeException {
  BusinessException(String message) {
    super(message);
  }
}
