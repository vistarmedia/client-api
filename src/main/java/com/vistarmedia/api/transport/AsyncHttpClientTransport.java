package com.vistarmedia.api.transport;

import java.io.IOException;
import java.net.URL;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import com.vistarmedia.api.ApiClient;


public class AsyncHttpClientTransport implements Transport {

  private static AsyncHttpClient client = new AsyncHttpClient();

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
