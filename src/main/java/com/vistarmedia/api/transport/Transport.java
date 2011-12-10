package com.vistarmedia.api.transport;

import java.io.IOException;
import java.net.URL;

public interface Transport {
  public void post(final URL url, final byte[] body, final TransportResponseHandler handler) throws IOException;
}
