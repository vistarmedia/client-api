package com.vistarmedia.api.transport;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Semaphore;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Response;
import com.vistarmedia.api.ApiClient;

/**
 * Transport implementation using Ning's async-http-client. This is the
 * preferred {@link Transport} to use, as we can rest assured its requests are
 * indeed happening asynchronously.
 */
public class AsyncHttpClientTransport implements Transport {

  private AsyncHttpClient client;
  private static final int MAX_CONNECTIONS = 3;
  private static final Semaphore conLock = new Semaphore(MAX_CONNECTIONS);
  private static final String USER_AGENT = String.format(
      "VistarClientAPI/%s.async", ApiClient.VERSION);

  public AsyncHttpClientTransport() {
    AsyncHttpClientConfig config = new AsyncHttpClientConfig.Builder()
        .setMaximumConnectionsTotal(MAX_CONNECTIONS).setUserAgent(USER_AGENT)
        .build();
    this.client = new AsyncHttpClient(config);
  }

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

  public void post(final URL url, final byte[] body,
                   final TransportResponseHandler handler) throws IOException {

    AsyncHttpClient.BoundRequestBuilder request = client.preparePost(url.toString()) //
      .setBody(body) //
      .addHeader("Content-Type", "application/octet-stream");

    executeRequest(request, handler);
  }

  public void get(URL url, final TransportResponseHandler handler) throws IOException {
    executeRequest(client.prepareGet(url.toString()), handler);
  }

  private void executeRequest(AsyncHttpClient.BoundRequestBuilder requestBuilder,
                              final TransportResponseHandler handler) throws IOException {
    try {
      conLock.acquire();
      requestBuilder.execute(new AsyncCompletionHandler<Void>() {

        @Override
        public void onThrowable(Throwable t) {
          conLock.release();
          handler.onThrowable(t);
        }

        @Override
        public Void onCompleted(Response response) throws Exception {
          conLock.release();
          handler.onResponse(response.getStatusCode(),
            response.getStatusText(), response.getResponseBodyAsStream());
          return null;
        }
      });
    } catch (InterruptedException e) {
      handler.onThrowable(new RuntimeException(
        "Interrupted acquiring connection lock"));
    }
  }

}
