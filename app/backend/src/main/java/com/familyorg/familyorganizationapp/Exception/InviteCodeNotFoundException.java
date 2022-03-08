package com.familyorg.familyorganizationapp.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InviteCodeNotFoundException extends RuntimeException {

  public InviteCodeNotFoundException() {
    super();
  }

  public InviteCodeNotFoundException(String message) {
    super(message);
  }

  public InviteCodeNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
