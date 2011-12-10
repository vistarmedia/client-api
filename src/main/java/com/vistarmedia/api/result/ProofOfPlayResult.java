package com.vistarmedia.api.result;

import com.vistarmedia.api.message.Api.ProofOfPlay;

public class ProofOfPlayResult extends ApiResult<ProofOfPlay> {
  public ProofOfPlayResult(ProofOfPlay proofOfPlay) {
    super(proofOfPlay);
  }
  
  public ProofOfPlayResult(ErrorResult error) {
    super(error);
  }
}
