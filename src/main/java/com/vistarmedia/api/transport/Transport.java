package com.vistarmedia.api.transport;

import java.io.IOException;
import java.net.URL;

import com.vistarmedia.api.ApiClient;

/**
 * Interface for objects which know how to move bytes over a wire. The
 * {@link ApiClient} assumes that the transport is async, but there are no
 * restrictions in place to ensure that is correct. For example -- an
 * implementation may make the request, block on the response, and immediately
 * invoke the {@link TransportResponseHandler}.
 */
public interface Transport {
  public void post(final URL url, final byte[] body,
      final TransportResponseHandler handler) throws IOException;
}
