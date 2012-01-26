package com.vistarmedia.api.result;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vistarmedia.api.message.Api.AdResponse;

public class ApiResultTest {

  @Test(expected = IllegalStateException.class)
  public void testGetResultOnError() {
    ErrorResult error = new ErrorResult(404, "Not Found");
    AdResponseResult res = new AdResponseResult(error);
    assertFalse(res.isSuccess());
    res.getResult();
  }

  @Test(expected = IllegalStateException.class)
  public void testGetErrorOnResult() {
    AdResponse resp = AdResponse.newBuilder().build();
    AdResponseResult result = new AdResponseResult(resp);
    assertTrue(result.isSuccess());
    result.getError();
  }

  @Test
  public void testGetError() {
    ErrorResult error = new ErrorResult(404, "Not Found");
    AdResponseResult res = new AdResponseResult(error);

    assertFalse(res.isSuccess());

    ErrorResult e = res.getError();
    assertEquals(404, e.getCode());
    assertEquals("Not Found", e.getMessage());
  }
}
