package com.vistarmedia.api.transport;

import java.io.InputStream;

/**
 * Interface used by a {@link Transport} to relay events to interested parties.
 * It is assumed these methods will be invoked in some other async pool thread,
 * but it is not guaranteed.
 */
public interface TransportResponseHandler {
  public void onThrowable(Throwable t);

  public void onResponse(int code, String message, InputStream body);
}
