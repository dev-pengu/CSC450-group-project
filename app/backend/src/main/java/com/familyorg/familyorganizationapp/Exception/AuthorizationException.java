package com.familyorg.familyorganizationapp.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthorizationException extends RuntimeException {

  private static final long serialVersionUID = 71198337282817719L;
  private boolean redirect = false;

  public AuthorizationException() {
    super();
    this.redirect = false;
  }

  public AuthorizationException(boolean redirect) {
    super();
    this.redirect = redirect;
  }

  public AuthorizationException(String message) {
    super(message);
    this.redirect = false;
  }

  public AuthorizationException(String message, boolean redirect) {
    super(message);
    this.redirect = redirect;
  }

  public AuthorizationException(String message, Throwable cause) {
    super(message, cause);
    this.redirect = false;;
  }

  public AuthorizationException(String message, Throwable cause, boolean redirect) {
    super(message, cause);
    this.redirect = redirect;
  }

  public boolean isRedirect() {
    return redirect;
  }

}
