package com.vistarmedia.api.transport;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

public class SuccessTransport implements Transport {

  private byte[] body;

  public SuccessTransport(byte[] body) {
    this.body = body;
  }

  public void post(URL url, byte[] body, TransportResponseHandler handler)
      throws IOException {
    handler.onResponse(200, "OK", new ByteArrayInputStream(this.body));
  }

  public void get(URL url, TransportResponseHandler handler) throws IOException {
    handler.onResponse(200, "OK", new ByteArrayInputStream(this.body));
  }

}
