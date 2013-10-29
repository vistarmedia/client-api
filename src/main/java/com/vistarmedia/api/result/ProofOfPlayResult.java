package com.vistarmedia.api.result;

import com.vistarmedia.api.message.Api.ProofOfPlayResponse;

public class ProofOfPlayResult extends ApiResult<ProofOfPlayResponse> {

  public ProofOfPlayResult(ProofOfPlayResponse result) {
    super(result);
  }

  public ProofOfPlayResult(ErrorResult error) {
    super(error); 
  }
}
