package com.vistarmedia.api.transport;

import java.io.IOException;
import java.net.URL;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import com.vistarmedia.api.ApiClient;

/**
 * Transport implementation using Ning's async-http-client. This is the
 * preferred {@link Transport} to use, as we can rest assured its requests are
 * indeed happening asynchronously.
 */
public class AsyncHttpClientTransport implements Transport {

  private static AsyncHttpClient client = new AsyncHttpClient();

  /**
   * Factory method to create a new {@link ApiClient} configured for this
   * transport.
   * 
   * @param host
   *          Vistar Media API endpoint host
   * @param port
   *          Vistar Media API endpoint port
   * @return a properly configured {@link ApiClient} for this transport.
   */
  public static ApiClient connect(String host, int port) {
    return new ApiClient(host, port, new AsyncHttpClientTransport());
  }

  @Override
  public void post(final URL url, final byte[] body,
      final TransportResponseHandler handler) throws IOException {
    client.preparePost(url.toString()).setBody(body)
        .execute(new AsyncCompletionHandler<Void>() {

          @Override
          public void onThrowable(Throwable t) {
            handler.onThrowable(t);
          }

          @Override
          public Void onCompleted(Response response) throws Exception {
            handler.onResponse(response.getStatusCode(),
                response.getStatusText(), response.getResponseBodyAsStream());
            return null;
          }
        });
  }

}
