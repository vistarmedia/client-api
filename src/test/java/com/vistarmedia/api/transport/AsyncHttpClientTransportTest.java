package com.vistarmedia.api.transport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;

import com.vistarmedia.api.ApiClient;
import com.vistarmedia.api.message.Api.AdRequest;
import com.vistarmedia.api.result.AdResponseResult;
import com.vistarmedia.api.result.ErrorResult;


public class AsyncHttpClientTransportTest {

  private ApiClient client;
  private long nowInSeconds;
  
  @Before
  public void setUp() {
    Transport transport = new AsyncHttpClientTransport();
    client = new ApiClient("localhost", 8123, transport);
    nowInSeconds = new Date().getTime() / 1000;
  }
  
  
  @Test
  public void testInvalidApiKey() throws InterruptedException, ExecutionException, TimeoutException {
    AdRequest adRequest = AdRequest.newBuilder()
      .setNetworkId("bad-network-id")
      .setApiKey("bad-api-key")
      .setDeviceId("device-id")
      .setDisplayTime(nowInSeconds)
      .setNumberOfScreens(1)
      .build();
    
    Future<AdResponseResult> resultFuture = client.sendAdRequest(adRequest);
    
    AdResponseResult result = resultFuture.get(1, TimeUnit.SECONDS);
    assertFalse(result.isSuccess());
    
    ErrorResult error = result.getError();
    assertEquals(403, error.getCode());
    assertEquals("Network not authorized", error.getMessage());
  }
}
