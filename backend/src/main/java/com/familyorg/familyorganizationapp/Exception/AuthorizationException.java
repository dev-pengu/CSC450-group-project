package com.familyorg.familyorganizationapp.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthorizationException extends ApiException {

  private static final long serialVersionUID = 71198337282817719L;
  private boolean redirect = false;

  public AuthorizationException(ApiExceptionCode apiCode, String message) {
    super(apiCode, message);
    this.redirect = false;
  }

  public AuthorizationException(ApiExceptionCode apiCode, String message, boolean redirect) {
    super(apiCode, message);
    this.redirect = redirect;
  }

  public AuthorizationException(ApiExceptionCode apiCode, String message, Throwable cause) {
    super(apiCode, message, cause);
    this.redirect = false;;
  }

  public AuthorizationException(ApiExceptionCode apiCode, String message, Throwable cause,
      boolean redirect) {
    super(apiCode, message, cause);
    this.redirect = redirect;
  }

  public boolean isRedirect() {
    return redirect;
  }

}
