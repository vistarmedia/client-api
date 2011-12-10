package com.vistarmedia.api;

/**
 * Exception used to store code/message information when encountering an error
 * for a synchronous {@link ApiClient} request. For async requests, this will be
 * encapsulated in the result.
 */
public class ApiRequestException extends Exception {
  private static final long serialVersionUID = -2107846945451416804L;

  private int               code;
  private String            message;

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
