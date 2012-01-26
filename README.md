# Vistar Media Client API
Below is the description of an API which allows clients to schedule real time
ads from [Vistar Media](http://www.vistarmedia.com/)'s ad servers. This document
will cover the core types and wire protocols, but implementation documentation
exists elsewhere.

## Overview


## Message Definitions
The API provides a number of serialization and deserialization formats. However,
each format will use the same set of model definitions and verbiage.

### AdRequest

An AdRequest announces to Vistar Media's system that you have free space and
time to show an ad. The specified time need to be "right now," but it should be


| Scope    | Type             | Field             |                            |
|----------|------------------|-------------------|----------------------------|
| required | string           | network_id        |                            |
| required | string           | api_key           |                            |
| required | string           | device_id         |                            |
| required | int64            | display_time      | UTC Epoch Seconds          |
| required | int32            | number_of_screens |                            |
| repeated | DeviceAttribute  | device_attribute  |                            |
| repeated | DisplayArea      | display_area      |                            |

For an AdRequest, the `network_id` and `api_key` fields must be obtained through
Vistar Media. If you do not have these, contact api@vistarmedia.com.



## API Client Implementations

### Java Client
There is a complete Java Client API [available on Github]
(https://github.com/vistarmedia/client-api). This
implementation has been tested in the field, and should be considered reliable.
Its [Javadocs](http://vistarmedia.github.com/client-api/reference/packages.html)
provide a number of examples.


~~~java
import foo.bar.baz
~~~

## Wire Protocols
All protocols go over HTTP, etc

### Json Protocol

~~~
POST /api/v1/get_ad/json HTTP/1.1
Host: dev.api.vistarmedia.com


{
  "network_id":       "24ba0582-7648-48b2-a7f4-0af3783b55f0",
  "api_key":          "eb7d6e26-5930-4fef-a3c7-aa023f31cefd",
  "device_id":        "device-5122",
  "display_time":     1322455356,
  "number_of_screens":1,
  "device_attribute": [
    {
      "name":  "zipcode",
      "value": "19122"
    },
    {
      "name":  "quality",
      "value": "pretty good"
    }
  ],
  "display_area":[
    {
      "id":          "Display-Area-1",
      "width":       800,
      "height":      600,
      "allow_audio": true,
      "supported_media": [
        "image/jpeg",
        "image/gif"
      ]
    },
    {
      "id":          "Display-Area-2",
      "width":       600,
      "height":      800,
      "allow_audio": true,
      "supported_media": [
        "image/jpeg",
        "image/gif"
      ]
    }
  ]
}


{
  "advertisement":[
    {
      "lease_id":          "618d6b97-a6c6-49d3-aacb-85ff3b2c9136",
      "lease_expiry":      1322655356,
      "display_area_id":   "Display-Area-1",
      "asset_id":          "ba3f-2ea0",
      "asset_url":         "http://assets.vistarmedia.com/r/g32/4.gif",
      "width":             800,
      "height":            600,
      "mimeType":          "image/gif",
      "length_in_seconds": 0
    },
    {
      "lease_id":          "97c99267-eee2-4de7-8bfc-5e2146784534",
      "lease_expiry":      1234582290,
      "display_area_id":   "Display-Area-2",
      "asset_id":          "24f0-83a3",
      "asset_url":         "http://assets.vistarmedia.com/g/2jc/3fa.jpeg",
      "width":             600,
      "height":            800,
      "mimeType":          "image/jpeg",
      "length_in_seconds": 0
    }
  ]
}
~~~

### XML Protocol

~~~xml
POST /api/v1/get_ad/xml HTTP/1.1
Host: dev.api.vistarmedia.com

<ad_request>
  <network_id>24ba0582-7648-48b2-a7f4-0af3783b55f0</network_id>
  <api_key>eb7d6e26-5930-4fef-a3c7-aa023f31cefd</api_key>
  <device_id>device-5122</device_id>
  <display_time>1322455356</display_time>
  <number_of_screens>1</number_of_screens>
  <device_attribute>
    <name>zipcode</name>
    <value>19122</value>
  </device_attribute>
  <device_attribute>
    <name>quality</name>
    <value>pretty good</value>
  </device_attribute>
  <display_area>
    <id>Display-Area-1</id>
    <width>800</width>
    <height>600</height>
    <allow_audio>true</allow_audio>
    <supported_media>image/gif</supported_media>
    <supported_media>image/jpeg</supported_media>
  </display_area>
  <display_area>
    <id>Display-Area-2</id>
    <width>600</width>
    <height>800</height>
    <allow_audio>true</allow_audio>
    <supported_media>image/gif</supported_media>
    <supported_media>image/jpeg</supported_media>
  </display_area>
</ad_request>


<AdResponse>
  <advertisement>
    <lease_id>8a7eb4f7-d63f-488f-bb02-a362c28d70a8</lease_id>
    <lease_expiry>1234582290</lease_expiry>
    <display_area_id>Display-Area-1</display_area_id>
    <asset_id>4</asset_id>
    <asset_url>http://assets.vistarmedia.com/r/g32/4.gif</asset_url>
    <width>800</width>
    <height>600</height>
    <mimeType>image/gif</mimeType>
    <length_in_seconds>0</length_in_seconds>
  </advertisement>
  <advertisement>
    <lease_id>4915f4f8-4920-4571-b6a5-f8c1bd748e68</lease_id>
    <lease_expiry>1234582290</lease_expiry>’
    <display_area_id>Display-Area-2</display_area_id>
    <asset_id>7</asset_id>
    <asset_url>http://assets.vistarmedia.com/g/2jc/3fa.jpeg</asset_url>
    <width>600</width>
    <height>800</height>
    <mimeType>image/jpeg</mimeType>
    <length_in_seconds>0</length_in_seconds>
  </advertisement>
</AdResponse>
~~~
