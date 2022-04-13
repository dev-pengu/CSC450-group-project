package com.familyorg.familyorganizationapp.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends ApiException {
  /**
   *
   */
  private static final long serialVersionUID = 4861083552743927051L;
  private boolean redirect = false;

  public UserNotFoundException(ApiExceptionCode apiCode) {
    super(apiCode);
  }

  public UserNotFoundException(ApiExceptionCode apiCode, String message) {
    super(apiCode, message);
  }

  public UserNotFoundException(ApiExceptionCode apiCode, String message, Throwable cause) {
    super(apiCode, message, cause);
  }

  public UserNotFoundException(ApiExceptionCode apiCode, boolean redirect) {
    super(apiCode);
    this.redirect = redirect;
  }

  public UserNotFoundException(ApiExceptionCode apiCode, String message, boolean redirect) {
    super(apiCode, message);
    this.redirect = redirect;
  }

  public UserNotFoundException(ApiExceptionCode apiCode, String message, Throwable cause,
      boolean redirect) {
    super(apiCode, message, cause);
    this.redirect = redirect;
  }

  public boolean isRedirect() {
    return redirect;
  }
}
