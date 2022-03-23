package com.familyorg.familyorganizationapp.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
  /**
   *
   */
  private static final long serialVersionUID = 4861083552743927051L;
  private boolean redirect = false;

  public UserNotFoundException() {
    super();
  }

  public UserNotFoundException(String message) {
    super(message);
  }

  public UserNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public UserNotFoundException(boolean redirect) {
    super();
    this.redirect = redirect;
  }

  public UserNotFoundException(String message, boolean redirect) {
    super(message);
    this.redirect = redirect;
  }

  public UserNotFoundException(String message, Throwable cause, boolean redirect) {
    super(message, cause);
    this.redirect = redirect;
  }

  public boolean isRedirect() {
    return redirect;
  }
}
