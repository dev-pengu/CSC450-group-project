package com.familyorg.familyorganizationapp.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class IncorrectCredentialsException extends ApiException {
  /**
   *
   */
  private static final long serialVersionUID = -2850539484606865023L;

  public IncorrectCredentialsException(ApiExceptionCode apiCode) {
    super(apiCode);
  }

  public IncorrectCredentialsException(ApiExceptionCode apiCode, String message) {
    super(apiCode, message);
  }

  public IncorrectCredentialsException(ApiExceptionCode apiCode, String message, Throwable cause) {
    super(apiCode, message, cause);
  }
}
