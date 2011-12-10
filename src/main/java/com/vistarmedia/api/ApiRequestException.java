package com.vistarmedia.api;

public class ApiRequestException extends Exception {
  private static final long serialVersionUID = -2107846945451416804L;

  private int code;
  private String message;
  
  public ApiRequestException(int code, String message) {
    super(String.format("(%s): %s", code, message));
    this.code = code;
    this.message = message;
  }
  
  public int getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }
}
