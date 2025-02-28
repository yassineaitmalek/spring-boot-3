package com.yatmk.persistence.exception.config;

public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException(String message) {
    super(message);
  }

  public ResourceNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public static void reThrow(Throwable throwable) {
    throw new ResourceNotFoundException(throwable.getMessage(), throwable);
  }

  public static ResourceNotFoundException of(Throwable throwable) {
    return new ResourceNotFoundException(throwable.getMessage(), throwable);
  }

  public static ResourceNotFoundException of(String message, Throwable cause) {
    return new ResourceNotFoundException(message, cause);
  }

  public static ResourceNotFoundException of(String message) {
    return new ResourceNotFoundException(message);
  }
}
