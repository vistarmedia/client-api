package com.vistarmedia.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.vistarmedia.api.future.ApiResultFuture;
import com.vistarmedia.api.message.Api.AdRequest;
import com.vistarmedia.api.message.Api.AdResponse;
import com.vistarmedia.api.message.Api.Advertisement;
import com.vistarmedia.api.message.Api.ProofOfPlay;
import com.vistarmedia.api.message.Api.ProofOfPlayResponse;
import com.vistarmedia.api.result.AdResponseResult;
import com.vistarmedia.api.result.ErrorResult;
import com.vistarmedia.api.result.ProofOfPlayResult;
import com.vistarmedia.api.transport.Transport;
import com.vistarmedia.api.transport.TransportResponseHandler;

/**
 * <p>
 * Simple client for interacting with Vistar Media's API servers. This client,
 * by default is asynchronous to help bulk loading speeds. However, depending on
 * the implementing transport, the calls may appear to be asynchronous but will
 * execute serially under the hood. It is recommended to use an
 * <code>ApiClient</code> with an
 * {@link com.vistarmedia.api.transport.AsyncHttpClientTransport
 * AsyncHttpClientTransport}.
 * </p>
 * 
 * <h4>Instantiating</h4>
 * <p>
 * Rather than creating an {@code ApiClient} by hand, it is often easier to ask
 * one of the provided factories to make one for you. This will insure that
 * {@link com.vistarmedia.api.transport.Transport} is initialized properly.
 * </p>
 * 
 * <pre class="pretty">
 * ApiClient client = {@link com.vistarmedia.api.transport.AsyncHttpClientTransport}.connect("dev.api.vistarmedia.com", 80);
 * </pre>
 * 
 * <p>
 * The {@code ApiClient} instances are safe to share across threads.
 * Instantiating multiple instances is safe to do, it will simply waste
 * resources.
 * </p>
 * 
 * <h4>Sending Ad Requests</h4>
 * <p>
 * There are two ways to send {@link com.vistarmedia.message.Api.AdRequest}s.
 * The simpler of the two is synchronously. However, it is far more inefficient.
 * Better throughput can be achieved by sending asynchronous requests to Vistar
 * Media's API server, it may lead to more complex code however.
 * </p>
 * 
 * <p>
 * Examples of the two styles are outlined below.
 * </p>
 * 
 * <h4>Synchronous Ad Requests</h4>
 * <p>
 * In the synchronous model, requests will be sent serially to Vistar Media's ad
 * server. Exceptions will be thrown directly at request time. There is also a
 * hard timeout of 10 seconds for any ad server communication. Requests
 * exceeding this window will be aborted and an exception will be raised.
 * </p>
 * 
 * <pre style="prettyprint">
 * import com.myco.AdRequestBuilder;
 * 
 * import com.vistarmedia.api.ApiClient;
 * import com.vistarmedia.api.ApiRequestException;
 * import com.vistarmedia.api.message.Api.AdRequest;
 * import com.vistarmedia.api.message.Api.Advertisement;
 * import com.vistarmedia.api.message.Api.AdResponse;
 * import com.vistarmedia.api.transport.AsyncHttpClientTransport;
 * 
 * public class Main {
 *   private ApiClient client;
 * 
 *   public Main(String host, int port) {
 *     client = AsyncHttpClientTransport.connect(host, port);
 *   }
 * 
 *   // Make 10 synchronous requests.
 *   public void run() throws ApiRequestException {
 *     for (int i = 0; i &lt; 10; i++) {
 *       AdRequest request = AdRequestBuidler.newRequest();
 *       AdResponse response = client.getAdResponse(request);
 * 
 *       for (Advertisement ad : response.getAdvertisementList) {
 *         System.out.println(&quot;Serving &quot; + ad);
 *       }
 *     }
 *   }
 * 
 *   public static void main(String[] args) {
 *     Main main = new Main(args[0], args[1]);
 *     try {
 *       main.run();
 *     } catch (ApiRequestException e) {
 *       System.err.println(&quot;Error talking to Vistar Media API&quot;);
 *       e.printStackTrace();
 *     }
 *   }
 * }
 * </pre>
 * 
 * 
 * <h4>Asynchronous Ad Requests</h4>
 * <p>
 * You may use the asynchronous API for bulk operations or more precise timing
 * control Below a sample asynchronous client will, as above, make ten requests
 * print the resulting {@link Advertisement}s.
 * </p>
 * 
 * <pre style="prettyprint">
 * import java.util.List;
 * import java.util.ArrayList;
 * import java.util.concurrent.Future;
 * import java.util.concurrent.TimeUnit;
 * 
 * import com.myco.AdRequestBuilder;
 * 
 * import com.vistarmedia.api.ApiClient;
 * import com.vistarmedia.api.ApiRequestException;
 * import com.vistarmedia.api.message.Api.AdRequest;
 * import com.vistarmedia.api.message.Api.Advertisement;
 * import com.vistarmedia.api.message.Api.AdResponse;
 * import com.vistarmedia.api.transport.AsyncHttpClientTransport;
 * 
 * public class Main {
 *   private ApiClient client;
 * 
 *   public Main(String host, int port) {
 *     client = AsyncHttpClientTransport.connect(host, port);
 *   }
 * 
 *   public void run() throws ApiRequestException {
 *     List&lt;Future&lt;AdResponseResult&gt;&gt; resultFutures = new ArrayList&lt;Future&lt;AdResponseResult&gt;&gt;();
 *     for (int i = 0; i &lt; 10; i++) {
 *       AdRequest request = AdRequestBuidler.newRequest();
 *       resultFutures.add(client.sendAdRequest(request));
 *     }
 *     
 *     for(Future&lt;AdResponseResult&gt; resultFuture : resultFutures) {
 *       AdResponseResult result = resultFuture.get(800, TimeUnit.MILLISECONDS);
 *       
 *       if(result.isSuccess()) {
 *         for (Advertisement ad : response.getAdvertisementList) {
 *           System.out.println(&quot;Serving &quot; + ad);
 *         }
 *       } else {
 *         ErrorResponse error = result.getError();
 *         System.err.println(&quot;Error talking to Vistar Media API&quot;)
 *         System.err.println(String.format(&quot;(%s): %s&quot;, error.getCode(), error.getMessage()); 
 *       }
 *     }
 *   }
 * 
 *   public static void main(String[] args) {
 *     Main main = new Main(args[0], args[1]);
 *     main.run();
 *   }
 * }
 * </pre>
 * 
 * <h4>Common HTTP Error Codes</h4>
 * 
 * <p>
 * The API servers will use standard HTTP response codes when sending results
 * back to the client. A few of the common ones are outlined below.
 * </p>
 * 
 * <table>
 * <thead>
 * <tr>
 * <th>Code</th>
 * <th>Description</th>
 * </tr>
 * </thead> <tbody>
 * <tr>
 * <td>{@code 200}</td>
 * <td><b>Response OK</b></td>
 * </tr>
 * <tr>
 * <td>{@code 400}</td>
 * <td>
 * <b>Invalid Request</b> Something was wrong with the data that was sent to the
 * server. Ensure all required fields are present and check the attached message
 * of the error.</td>
 * </tr>
 * <tr>
 * <td>{@code 408}</td>
 * <td>
 * <b>Request Timeout</b> The client may be having problems connecting to the
 * internet, or Vistar's API servers may not be responding quickly enough. This
 * request should be retried.</td>
 * </tr>
 * <tr>
 * <td>{@code 500}</td>
 * <td>
 * <b>Server Error</b> The Vistar Media API servers received the requested, but
 * encountered an error while processing it. This request may be retried.</td>
 * </tr>
 * <tr>
 * <td>{@code 503}</td>
 * <td>
 * <b>Gateway Timeout</b> Vistar's load balancers will return 503's when talking
 * to the development stack ({@code dev.api.vistarmedia.com}) if the cluster is
 * in the middle of a roll. This request should be retried.</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 */
public class ApiClient {

  private String host;
  private int port;
  private Transport transport;
  private int syncTimeoutSeconds;

  public static final String VERSION = "1.5.1";
  private static final String GET_AD_PATH = "/api/v1/get_ad/protobuf";
  private static final String BULK_POP_PATH = "/api/v1/proof_of_play/batch/protobuf";

  public ApiClient(String host, int port, Transport transport,
      int syncTimeoutSeconds) {
    this.host = host;
    this.port = port;
    this.transport = transport;
    this.syncTimeoutSeconds = syncTimeoutSeconds;
  }

  /**
   * <p>
   * Instantiate an ApiClient directly. A transport's factory method should
   * instead be used, but this method of creation will work.
   * </p>
   * 
   * <p>
   * This will configure the client to time out after 10 seconds for synchronous
   * requests.
   * </p>
   * 
   * @param host
   *          Vistar Media API host name, ie: {@code dev.api.vistarmedia.com}
   * @param port
   *          Port to connect to the Vistar Media server, nearly always 80
   * @param transport
   *          Transport implementation to send requests over.
   */
  public ApiClient(String host, int port, Transport transport) {
    this(host, port, transport, 10);
  }

  /**
   * Asynchronously send an {@link com.vistarmedia.api.message.Api.AdRequest}
   * over the configured transport to the Vistar Media API server. This will
   * return a result future which will be filled at some point in the
   * background. The {@link com.vistarmedia.api.result.AdResponseResult} may
   * contain either an {@link com.vistarmedia.api.message.Api.AdResponse} or an
   * {@link com.vistarmedia.api.result.ErrorResult} describing what went wrong
   * during the operation.
   * 
   * @param request
   *          Request that will be asynchronously sent to the Vistar Media API
   *          servers
   * @return A Future containing the eventual result of this operation, be it an
   *         error or success.
   */
  public Future<AdResponseResult> sendAdRequest(AdRequest request) {
    final ApiResultFuture<AdResponseResult> result = new ApiResultFuture<AdResponseResult>();
    TransportResponseHandler handler = new TransportResponseHandler() {

      public void onThrowable(Throwable t) {
        onError(400, t.getLocalizedMessage());
      }

      public void onResponse(int code, String message, InputStream body) {
        if (code == 200) {
          try {
            AdResponse response = AdResponse.parseFrom(body);
            result.fulfill(new AdResponseResult(response));
          } catch (IOException e) {
            onError(500, e.getLocalizedMessage());
          }

        } else {
          onError(code, message);
        }
      }

      private void onError(int code, String message) {
        ErrorResult error = new ErrorResult(code, message);
        result.fulfill(new AdResponseResult(error));
      }
    };

    sendRequest(GET_AD_PATH, request.toByteArray(), handler);
    return result;
  }

  /**
   * Asynchronously send the proof of play for an {@code Advertisement} over the
   * configured transport to the Vistar Media API server. This will return a
   * result future which will be filled at some point in the background. The
   * {@link com.vistarmedia.api.result.ProofOfPlayResult} may contain either a
   * Boolean indicating if the lease was valid or a
   * {@link com.vistarmedia.api.result.ErrorResult} describing what went wrong
   * during the operation.
   * 
   * @param ad
   *          Advertisement which has successfully been shown
   * @return A Future containing the eventual result of this operation, be it an
   *         error or success.
   */
  public Future<ProofOfPlayResult> sendProofOfPlay(Advertisement ad) {
    final ApiResultFuture<ProofOfPlayResult> result = new ApiResultFuture<ProofOfPlayResult>();

    try {
      URL url = new URL(ad.getProofOfPlayUrl());
      transport.get(url, getProofOfPlayResponseHandler(result));
    } catch (Throwable t) {
      ErrorResult error = new ErrorResult(500, "Invalid URL");
      result.fulfill(new ProofOfPlayResult(error));
    }
    return result;
  }

  public Future<ProofOfPlayResult> sendProofsOfPlay(
      Collection<Advertisement> ads) {
    final ApiResultFuture<ProofOfPlayResult> result = new ApiResultFuture<ProofOfPlayResult>();
    StringBuffer body = new StringBuffer();
    for (Advertisement ad : ads) {
      body.append(ad.getProofOfPlayUrl());
      body.append("\n");
    }

    try {
      URL url = createUrl(BULK_POP_PATH);
      transport.post(url, body.toString().getBytes(),
          getProofOfPlayResponseHandler(result));
    } catch (Throwable t) {
      ErrorResult error = new ErrorResult(500, "Invalid URL");
      result.fulfill(new ProofOfPlayResult(error));
    }
    return result;
  }

  /**
   * Asynchronously send the proof of play expiration for an
   * {@link Advertisement}. The resulting {@link ProofOfPlay} will have a
   * non-zero value in its <cc>expires</cc> field.
   * 
   * @param ad
   * @return
   */
  public Future<ProofOfPlayResult> expire(Advertisement ad) {
    final ApiResultFuture<ProofOfPlayResult> result = new ApiResultFuture<ProofOfPlayResult>();

    try {
      URL url = new URL(ad.getExpirationUrl());
      transport.get(url, getProofOfPlayResponseHandler(result));
    } catch (Throwable t) {
      ErrorResult error = new ErrorResult(500, "Invalid URL");
      result.fulfill(new ProofOfPlayResult(error));
    }
    return result;
  }

  /**
   * Asynchronously send the proof of play for an {@code Advertisement} over the
   * configured transport to the Vistar Media API server, passing an actual
   * display time. This will return a result future which will be filled at some
   * point in the background. The
   * {@link com.vistarmedia.api.result.ProofOfPlayResult} may contain either a
   * Boolean indicating if the lease was valid or a
   * {@link com.vistarmedia.api.result.ErrorResult} describing what went wrong
   * during the operation.
   * 
   * @param ad
   *          Advertisement which has successfully been shown
   * @param displayTime
   *          Epoch time of actual display time
   * @return A Future containing the eventual result of this operation, be it an
   *         error or success.
   */
  public Future<ProofOfPlayResult> sendProofOfPlay(Advertisement ad,
      int displayTime) {
    final ApiResultFuture<ProofOfPlayResult> result = new ApiResultFuture<ProofOfPlayResult>();

    try {
      URL url = new URL(ad.getProofOfPlayUrl());
      ProofOfPlay pop = ProofOfPlay.newBuilder().setDisplayTime(displayTime)
          .build();
      transport.post(url, pop.toByteArray(),
          getProofOfPlayResponseHandler(result));
    } catch (Throwable t) {
      ErrorResult error = new ErrorResult(500, "Invalid URL");
      result.fulfill(new ProofOfPlayResult(error));
    }
    return result;
  }

  private TransportResponseHandler getProofOfPlayResponseHandler(
      final ApiResultFuture<ProofOfPlayResult> result) {
    return new TransportResponseHandler() {

      public void onThrowable(Throwable t) {
        onError(400, t.getLocalizedMessage());
      }

      public void onResponse(int code, String message, InputStream body) {
        if (code == 200 || code == 204 || code == 400) {
          ProofOfPlayResponse resp;
          try {
            resp = ProofOfPlayResponse.parseFrom(body);
            result.fulfill(new ProofOfPlayResult(resp));
          } catch (IOException e) {
            onError(500, e.toString());
          }
        } else {
          onError(code, message);
        }
      }

      private void onError(int code, String message) {
        ErrorResult error = new ErrorResult(code, message);
        result.fulfill(new ProofOfPlayResult(error));
      }
    };
  }

  /**
   * <p>
   * Synchronously get an {@code AdResponse}. This will respond within the
   * default timeout (10 seconds) or throw an exception. If there is any problem
   * with the request, an {@link com.vistarmedia.api.ApiRequestException} will
   * be thrown with the HTTP code and a string describing the problem.
   * </p>
   * 
   * @param request
   * @return A valid AdResponse from the Vistar Media API servers.
   * @throws ApiRequestException
   *           thrown when there was any problem with the request.
   */
  public AdResponse getAdResponse(AdRequest request) throws ApiRequestException {
    Future<AdResponseResult> resultFuture = sendAdRequest(request);
    AdResponseResult result;
    try {
      result = resultFuture.get(syncTimeoutSeconds, TimeUnit.SECONDS);
    } catch (Throwable t) {
      throw new ApiRequestException(408, t.getLocalizedMessage());
    }

    if (result == null) {
      throw new ApiRequestException(408, "Request Timeout");
    }

    if (result.isSuccess()) {
      return result.getResult();
    } else {
      ErrorResult error = result.getError();
      throw new ApiRequestException(error.getCode(), error.getMessage());
    }
  }

  /**
   * <p>
   * Synchronously sends the Proof of Play for an {@code Advertisement}. This
   * will respond within the default timeout (10 seconds) or throw an exception.
   * If there is any problem with the request, an
   * {@link com.vistarmedia.api.ApiRequestException} will be thrown with the
   * HTTP code and a string describing the problem.
   * </p>
   * 
   * @param ad
   * @return A {@code ProofOfPlayResponse} summarizing the transaction
   * @throws ApiRequestException
   *           thrown when there was any problem with the request.
   */
  public ProofOfPlayResponse getProofOfPlay(Advertisement ad) throws ApiRequestException {
    Future<ProofOfPlayResult> resultFuture = sendProofOfPlay(ad);
    return processProofOfPlayFuture(resultFuture);
  }

  /**
   * <p>
   * Synchronously sends the Proof of Play for an {@code Advertisement} with an
   * overridden actual display time. This will respond within the default
   * timeout (10 seconds) or throw an exception. If there is any problem with
   * the request, an {@link com.vistarmedia.api.ApiRequestException} will be
   * thrown with the HTTP code and a string describing the problem.
   * </p>
   * 
   * @param ad
   * @param displayTime
   * @return A {@code ProofOfPlayResponse} summarizing the transaction
   * @throws ApiRequestException
   *           thrown when there was any problem with the request.
   */
  public ProofOfPlayResponse getProofOfPlay(Advertisement ad, int displayTime)
      throws ApiRequestException {
    Future<ProofOfPlayResult> resultFuture = sendProofOfPlay(ad, displayTime);
    return processProofOfPlayFuture(resultFuture);
  }

  private ProofOfPlayResponse processProofOfPlayFuture(
      Future<ProofOfPlayResult> resultFuture) throws ApiRequestException {
    ProofOfPlayResult result;
    try {
      result = resultFuture.get(syncTimeoutSeconds, TimeUnit.SECONDS);
    } catch (Throwable t) {
      throw new ApiRequestException(408, t.getLocalizedMessage());
    }

    if (result == null) {
      throw new ApiRequestException(408, "Request Timeout");
    }

    if (result.isSuccess()) {
      return result.getResult();
    } else {
      ErrorResult error = result.getError();
      throw new ApiRequestException(error.getCode(), error.getMessage());
    }
  }

  /**
   * <p>
   * Implementation to have the {@code Transport} start handling a request at
   * the given path (relative to the provided host/port fields) and body. This
   * will simply kick off the request, no provide any result. Results are
   * indirectly routed to the handler
   * </p>
   * 
   * @param path
   *          Relative path of the request
   * @param body
   *          {@code POST} body to send to the Vistar Media API server
   * @param handler
   *          Handler which will be called whenever there is a successful
   *          response or error.
   */
  private void sendRequest(String path, byte[] body,
      TransportResponseHandler handler) {

    URL url;
    try {
      url = createUrl(path);
    } catch (MalformedURLException e) {
      handler.onThrowable(e);
      return;
    }

    try {
      transport.post(url, body, handler);
    } catch (IOException e) {
      handler.onThrowable(e);
    }
  }

  /**
   * <p>
   * Convert a relative path to an absolute URL given the provided {@code host}
   * and {@code port} fields. Unless instantiated with a completely invalid
   * hostname, this should not throw errors.
   * </p>
   * 
   * @param path
   *          relative path for the generated {@code URL}.
   * @return Absolute {@code URL} based on the {@code host} and {@code port}
   *         fields with the given relative {@code path}
   * @throws MalformedURLException
   *           Thrown in the case that the resultign {@code URL} is not valid.
   *           This should only happen if the {@code ApiClient} has been
   *           instantiated with a host name that is completely invalid (ie:
   *           containing a colon or a slash).
   */
  private URL createUrl(String path) throws MalformedURLException {
    String urlString = String.format("http://%s:%s%s", host, port, path);
    return new URL(urlString);
  }

}
