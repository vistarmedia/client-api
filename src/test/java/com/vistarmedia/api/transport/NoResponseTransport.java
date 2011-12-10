package com.vistarmedia.api.transport;

import java.io.IOException;
import java.net.URL;

public class NoResponseTransport implements Transport {

  @Override
  public void post(URL url, byte[] body, TransportResponseHandler handler)
      throws IOException {
  }

}
