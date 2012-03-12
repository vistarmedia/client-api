package com.vistarmedia.api.transport;

import java.io.IOException;
import java.net.URL;

public class NoResponseTransport implements Transport {

  public void post(URL url, byte[] body, TransportResponseHandler handler)
      throws IOException {
  }

  public void get(URL url, TransportResponseHandler handler) throws IOException {

  }

}
