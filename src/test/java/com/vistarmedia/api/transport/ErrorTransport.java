package com.vistarmedia.api.transport;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ErrorTransport implements Transport {

  private int         code;
  private String      message;
  private InputStream EMPTY = new ByteArrayInputStream("".getBytes());

  public ErrorTransport(int code, String message) {
    this.code = code;
    this.message = message;
  }

  public void post(URL url, byte[] body, TransportResponseHandler handler)
      throws IOException {
    handler.onResponse(code, message, EMPTY);
  }

  public void get(URL url, TransportResponseHandler handler) throws IOException {
    handler.onResponse(code, message, EMPTY);
  }

}
