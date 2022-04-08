package com.familyorg.familyorganizationapp.Exception;

public abstract class ApiException extends RuntimeException {

  private static final long serialVersionUID = -7672258751098787557L;

  protected ApiExceptionCode apiCode;

  public ApiException(ApiExceptionCode apiCode) {
    super();
    this.apiCode = apiCode;
  }

  public ApiException(ApiExceptionCode apiCode, String message) {
    super(message);
    this.apiCode = apiCode;
  }

  public ApiException(ApiExceptionCode apiCode, String message, Throwable cause) {
    super(message, cause);
    this.apiCode = apiCode;
  }

  public ApiExceptionCode getApiCode() {
    return this.apiCode;
  }
}
