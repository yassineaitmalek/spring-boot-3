package com.yatmk.persistence.exception.config;

public class ServerSideException extends RuntimeException {

  public ServerSideException(Exception e) {
    super(e.getMessage(), e);
  }

  public ServerSideException(String message, Throwable cause) {
    super(message, cause);
  }

  public ServerSideException(String message) {
    super(message);
  }

  public static void reThrow(Throwable throwable) {
    throw new ServerSideException(throwable.getMessage(), throwable);
  }

  public static ServerSideException of(Throwable throwable) {
    return new ServerSideException(throwable.getMessage(), throwable);
  }

  public static ServerSideException of(String message, Throwable cause) {
    return new ServerSideException(message, cause);
  }

  public static ServerSideException of(String message) {
    return new ServerSideException(message);
  }
}
