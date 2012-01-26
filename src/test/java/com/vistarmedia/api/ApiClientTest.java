package com.vistarmedia.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;

import com.vistarmedia.api.message.Api.AdRequest;
import com.vistarmedia.api.message.Api.AdResponse;
import com.vistarmedia.api.message.Api.ProofOfPlay;
import com.vistarmedia.api.result.AdResponseResult;
import com.vistarmedia.api.result.ErrorResult;
import com.vistarmedia.api.result.ProofOfPlayResult;
import com.vistarmedia.api.transport.ErrorTransport;
import com.vistarmedia.api.transport.NoResponseTransport;
import com.vistarmedia.api.transport.SuccessTransport;
import com.vistarmedia.api.transport.Transport;

public class ApiClientTest {

  private AdRequest   sampleAdRequest;
  private ProofOfPlay sampleProofOfPlay;

  @Before
  public void setUp() {
    long nowInSeconds = new Date().getTime() / 1000;
    sampleAdRequest = AdRequest.newBuilder().setNetworkId("network-id")
        .setApiKey("api-key").setDeviceId("internal-device-id")
        .setNumberOfScreens(1).setDisplayTime(nowInSeconds)
        .setDirectConnection(true).build();

    sampleProofOfPlay = ProofOfPlay.newBuilder().setDisplayDurationSeconds(10)
        .setDisplayTime(nowInSeconds).setNumberOfScreens(1)
        .setDirectConnection(true).setLeaseId("sample-lease-id").build();
  }

  @Test
  public void testSimpleAdResponse() throws InterruptedException,
      ExecutionException {
    byte[] responseBody = AdResponse.newBuilder().build().toByteArray();
    Transport transport = new SuccessTransport(responseBody);
    ApiClient client = new ApiClient("host", 80, transport);

    Future<AdResponseResult> resultFuture = client
        .sendAdRequest(sampleAdRequest);
    AdResponseResult result = resultFuture.get();

    assertTrue(result.isSuccess());
    AdResponse resp = result.getResult();
    assertNotNull(resp);
  }

  @Test
  public void testSimpleProofOfPlay() throws InterruptedException,
      ExecutionException {
    byte[] responseBody = sampleProofOfPlay.toByteArray();
    Transport transport = new SuccessTransport(responseBody);
    ApiClient client = new ApiClient("host", 80, transport);

    Future<ProofOfPlayResult> resultFuture = client
        .sendProofOfPlay(sampleProofOfPlay);
    ProofOfPlayResult result = resultFuture.get();

    assertTrue(result.isSuccess());
    ProofOfPlay resp = result.getResult();
    assertNotNull(resp);
  }

  @Test
  public void testInvalidResponseBody() throws InterruptedException,
      ExecutionException {
    Transport transport = new SuccessTransport("invalid".getBytes());
    ApiClient client = new ApiClient("host", 80, transport);

    AdResponseResult result = client.sendAdRequest(sampleAdRequest).get();
    assertFalse(result.isSuccess());

    ErrorResult error = result.getError();
    assertEquals(500, error.getCode());
    assertThat(error.getMessage(),
        containsString("the input ended unexpectedly"));
  }

  @Test(expected = TimeoutException.class)
  public void testResponseTimeout() throws InterruptedException,
      ExecutionException, TimeoutException {
    Transport transport = new NoResponseTransport();
    ApiClient client = new ApiClient("host", 80, transport);

    Future<AdResponseResult> resultFuture = client
        .sendAdRequest(sampleAdRequest);
    assertFalse(resultFuture.isDone());

    resultFuture.get(100, TimeUnit.MILLISECONDS);
  }

  @Test
  public void testRequestError() throws InterruptedException,
      ExecutionException {
    Transport transport = new ErrorTransport(404, "Not Found");
    ApiClient client = new ApiClient("host", 80, transport);

    AdResponseResult result = client.sendAdRequest(sampleAdRequest).get();
    assertFalse(result.isSuccess());

    ErrorResult error = result.getError();
    assertEquals(404, error.getCode());
    assertEquals("Not Found", error.getMessage());
  }

  @Test
  public void testSimpleSynchronousAdRequest() throws ApiRequestException {
    byte[] responseBody = AdResponse.newBuilder().build().toByteArray();
    Transport transport = new SuccessTransport(responseBody);
    ApiClient client = new ApiClient("host", 80, transport);

    AdResponse response = client.getAdResponse(sampleAdRequest);
    assertNotNull(response);
  }

  @Test
  public void testMalformedSyncResponse() {
    Transport transport = new SuccessTransport("invalid".getBytes());
    ApiClient client = new ApiClient("host", 80, transport);

    try {
      client.getAdResponse(sampleAdRequest);
    } catch (ApiRequestException e) {
      assertEquals(500, e.getCode());
      assertThat(e.getMessage(), containsString("the input ended unexpectedly"));
      return;
    }
    fail("Exception should have been thrown");
  }

  @Test
  public void testSyncResponseTimeout() {
    Transport transport = new NoResponseTransport();
    ApiClient client = new ApiClient("host", 80, transport, 0);

    try {
      client.getAdResponse(sampleAdRequest);
    } catch (ApiRequestException e) {
      assertEquals(408, e.getCode());
      assertEquals(e.getMessage(), "Response timed out");
      return;
    }
    fail("Exception should have been thrown");
  }
}
