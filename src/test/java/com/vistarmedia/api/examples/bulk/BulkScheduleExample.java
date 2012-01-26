package com.vistarmedia.api.examples.bulk;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import com.vistarmedia.api.ApiClient;
import com.vistarmedia.api.message.Api.AdRequest;
import com.vistarmedia.api.message.Api.DeviceAttribute;
import com.vistarmedia.api.message.Api.DisplayArea;
import com.vistarmedia.api.transport.AsyncHttpClientTransport;

public class BulkScheduleExample {

  private String                networkId;
  private String                apiKey;
  private ApiClient             vistarApiClient;
  private List<DeviceAttribute> deviceAttributes;
  private DisplayArea           displayArea;
  private AdResponseHandler     adResponseHandler;

  public BulkScheduleExample(AdResponseHandler adResponseHandler,
      String networkId, String apiKey, String host, int port) {
    this.networkId = networkId;
    this.apiKey = apiKey;
    this.adResponseHandler = adResponseHandler;

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

  protected void requestRange(Calendar start, Calendar end) throws Exception {
    Calendar current = (Calendar) start.clone();

    while (current.before(end)) {
      AdRequest request = AdRequest.newBuilder().setNetworkId(networkId)
          .setApiKey(apiKey).setDeviceId("device-122").setNumberOfScreens(2)
          .addAllDeviceAttribute(deviceAttributes).addDisplayArea(displayArea)
          .setDisplayTime(current.getTimeInMillis() / 1000).build();
      adResponseHandler.handle(vistarApiClient.sendAdRequest(request));
      current.add(Calendar.MINUTE, 1);
    }
  }

  public static void main(String[] args) throws Exception {
    String networkId = "24ba0582-7648-48b2-a7f4-0af3783b55f0";
    String apiKey = "eb7d6e26-5930-4fef-a3c7-aa023f31cefd";

    AdResponseHandler adResponseHandler = new AdResponseHandler();
    new Thread(adResponseHandler).start();

    BulkScheduleExample example = new BulkScheduleExample(adResponseHandler,
        networkId, apiKey, "dev.api.vistarmedia.com", 80);

    Calendar start = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    Calendar end = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    end.add(Calendar.DATE, 100);

    example.requestRange(start, end);

    adResponseHandler.stop();
    return;
  }
}
