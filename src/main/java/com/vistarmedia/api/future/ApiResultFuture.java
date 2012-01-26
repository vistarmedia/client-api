package com.vistarmedia.api.future;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.vistarmedia.api.result.ApiResult;

/**
 * <p>
 * Future containing API results. Calling
 * {@link ApiResultFuture#cancel(boolean)} will have no effect on this class.
 * </p>
 * 
 * <p>
 * This class should not be exposed outside of this API. Rather, a
 * {@code Future<T>}. This is because the {@link #fulfill(ApiResult)} method
 * should only be used inside of this api.
 * </p>
 * 
 * @param <T>
 *          Type of {@link ApiResult} this future contains.
 */
public class ApiResultFuture<T extends ApiResult<?>> implements Future<T> {

  private T              apiResult;
  private CountDownLatch latch  = new CountDownLatch(1);
  private AtomicBoolean  isDone = new AtomicBoolean(false);

  /**
   * Fulfill the future. This method should only be called inside of this API.
   * Classes returning a {@link ApiResultFuture} should expose an interface of
   * {@link java.util.concurrent.Future} so that this method call will be
   * hidden.
   * 
   * @param apiResult
   *          Value to fill this {@link java.util.concurrent.Future} with.
   */
  public void fulfill(T apiResult) {
    if (!isDone.get()) {
      this.apiResult = apiResult;
      isDone.set(true);
      latch.countDown();
    }
  }

  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    return false;
  }

  @Override
  public T get() throws InterruptedException, ExecutionException {
    latch.await();
    return apiResult;
  }

  @Override
  public T get(long timeout, TimeUnit unit) throws InterruptedException,
      ExecutionException, TimeoutException {
    if (!latch.await(timeout, unit)) {
      throw new TimeoutException("Response timed out");
    }
    return apiResult;
  }

  @Override
  public boolean isCancelled() {
    return false;
  }

  @Override
  public boolean isDone() {
    return isDone.get();
  }
}
