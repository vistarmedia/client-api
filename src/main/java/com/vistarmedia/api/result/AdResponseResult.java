package com.vistarmedia.api.result;

import com.vistarmedia.api.message.Api.AdResponse;

public class AdResponseResult extends ApiResult<AdResponse> {

  public AdResponseResult(AdResponse result) {
    super(result);
  }
  
  public AdResponseResult(ErrorResult error) {
    super(error);
  }
}
