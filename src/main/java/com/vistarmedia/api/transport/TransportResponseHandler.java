package com.vistarmedia.api.transport;

import java.io.InputStream;

public interface TransportResponseHandler {
  public void onThrowable(Throwable t);
  public void onResponse(int code, String message, InputStream body);
}
