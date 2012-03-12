package com.vistarmedia.api.result;

public class ProofOfPlayResult extends ApiResult<Boolean> {

  public ProofOfPlayResult(Boolean result) {
    super(result);
  }

  public ProofOfPlayResult(ErrorResult error) {
    super(error);
  }
}
