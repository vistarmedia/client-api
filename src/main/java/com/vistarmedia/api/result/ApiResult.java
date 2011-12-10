package com.vistarmedia.api.result;

/**
 * <p>
 * A Union type for API responses that can either represent an successful
 * response or some error condition. This allows consuming code to inspect
 * potential errors without resorting to random exception catching and hopeful
 * casts. This will hold either the result of the API request, or an error
 * describing what happened. The {@link #isSuccess()} before calling
 * {@link #getResult()} or {@link #getError()}, otherwise and
 * {@code IllegalStateException} may be thrown.
 * </p>
 * 
 * <p>
 * A canonical example would be as follows
 * </p>
 * 
 * <pre class="prettyprint">
 * ApiResult&lt;AdResponse&gt; result = client.getAdResponse(req);
 * 
 * if (result.isSuccess()) {
 *   handleAdResponse(result.getResult());
 * } else {
 *   handleError(result.getError());
 * }
 * </pre>
 * 
 * @param <T>
 *          "Success" type wrapped by this <cc>ApiResult</cc>.
 */
public abstract class ApiResult<T> {

  private boolean     success;
  private T           result;
  private ErrorResult error;

  public ApiResult(T result) {
    this.success = true;
    this.result = result;
  }

  public ApiResult(ErrorResult error) {
    this.success = false;
    this.error = error;
  }

  public boolean isSuccess() {
    return success;
  }

  public T getResult() {
    if (success) {
      return result;
    } else {
      throw new IllegalStateException("Called get on an invalid ApiResult");
    }
  }

  public ErrorResult getError() {
    if (!success) {
      return error;
    } else {
      throw new IllegalStateException("Ccalled getError on a valid ApiResult");
    }
  }
}
