package com.vistarmedia.api.result;

public class ErrorResult {
  
  private int code;
  private String message;
  
  public ErrorResult(int code, String message) {
    this.code = code;
    this.message = message;
  }

  public int getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }
  
  @Override
  public String toString() {
    return String.format("ErrorResult(%s): %s", code, message);
  }
}
