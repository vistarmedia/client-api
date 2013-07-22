package com.vistarmedia.api.examples;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.vistarmedia.api.ApiClient;
import com.vistarmedia.api.message.Api.AdRequest;
import com.vistarmedia.api.message.Api.AdResponse;
import com.vistarmedia.api.message.Api.Advertisement;
import com.vistarmedia.api.message.Api.DeviceAttribute;
import com.vistarmedia.api.message.Api.DisplayArea;
import com.vistarmedia.api.result.AdResponseResult;
import com.vistarmedia.api.result.ErrorResult;
import com.vistarmedia.api.result.ProofOfPlayResult;
import com.vistarmedia.api.transport.AsyncHttpClientTransport;

public class SimpleAdRequestExample {

  private String                networkId;
  private String                apiKey;
  private ApiClient             vistarApiClient;
  private List<DeviceAttribute> deviceAttributes;
  private DisplayArea           displayArea;

  public SimpleAdRequestExample(String networkId, String apiKey, String host,
      int port) {
    this.networkId = networkId;
    this.apiKey = apiKey;
    vistarApiClient = AsyncHttpClientTransport.connect(host, port);
    deviceAttributes = new ArrayList<DeviceAttribute>();

    deviceAttributes.add(DeviceAttribute.newBuilder().setName("zipcode")
        .setValue("19125").build());

    deviceAttributes.add(DeviceAttribute.newBuilder().setName("venue")
        .setValue("Chick-fil-A").build());

    displayArea = DisplayArea.newBuilder().setId("default-area")
        .setAllowAudio(true).setWidth(1280).setHeight(760)
        .addSupportedMedia("image/gif").addSupportedMedia("image/jpeg")
        .addSupportedMedia("image/png")
        .addSupportedMedia("application/x-shockwave-flash")
        .addSupportedMedia("video/x-flv").addSupportedMedia("video/mp4")
        .build();
  }

  public SimpleAdRequestExample(String networkId, String apiKey, String host) {
    this(networkId, apiKey, host, 80);
  }

  protected void requestRange(Calendar start, Calendar end) throws Exception {
    Calendar current = (Calendar) start.clone();
    List<Future<AdResponseResult>> resultFutures = new ArrayList<Future<AdResponseResult>>();
    List<Future<ProofOfPlayResult>> popFutures = new ArrayList<Future<ProofOfPlayResult>>();

    while (current.before(end)) {
      AdRequest request = AdRequest.newBuilder().setNetworkId(networkId)
          .setApiKey(apiKey).setDeviceId("device-1235")
          .addDisplayArea(displayArea)
          .setDisplayTime(current.getTimeInMillis() / 1000).build();

      resultFutures.add(vistarApiClient.sendAdRequest(request));
      current.add(Calendar.MINUTE, 20);
    }

    for (Future<AdResponseResult> resultFuture : resultFutures) {
      AdResponseResult result = resultFuture.get(500, TimeUnit.MILLISECONDS);
      if (result == null) {
        System.err.print("Response Timeout");
      } else {
        if (result.isSuccess()) {
          popFutures.addAll(handleAdResponse(result.getResult()));
        } else {
          handleError(result.getError());
        }
      }
    }

    for (Future<ProofOfPlayResult> popFuture : popFutures) {
      ProofOfPlayResult result = popFuture.get(1, TimeUnit.SECONDS);
      if (result != null && result.isSuccess()) {
        Boolean valid = result.getResult();
        System.out.println("Spent Lease: " + valid);
      }
    }
  }

  private List<Future<ProofOfPlayResult>> handleAdResponse(AdResponse response) {
    List<Future<ProofOfPlayResult>> resultFutures = new ArrayList<Future<ProofOfPlayResult>>();
    for (Advertisement ad : response.getAdvertisementList()) {
      System.out.println("Pretending to show: " + ad.getAssetUrl());
      resultFutures.add(vistarApiClient.sendProofOfPlay(ad));
    }
    return resultFutures;
  }

  private void handleError(ErrorResult result) {
    System.err.println(result.toString());
  }

  public static void main(String[] args) throws Exception {
    String networkId = "24ba0582-7648-48b2-a7f4-0af3783b55f0";
    String apiKey = "eb7d6e26-5930-4fef-a3c7-aa023f31cefd";

    SimpleAdRequestExample example = new SimpleAdRequestExample(networkId,
        apiKey, "dev.api.vistarmedia.com", 80);

    Calendar start = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    Calendar end = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    end.add(Calendar.DATE, 1000);

    example.requestRange(start, end);
    System.out.println("Done!");
    return;
  }

}
