package com.familyorg.familyorganizationapp.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ExistingUserException extends ApiException {

  private static final long serialVersionUID = -1570394825629179223L;

  public ExistingUserException(ApiExceptionCode apiCode) {
    super(apiCode);
  }

  public ExistingUserException(ApiExceptionCode apiCode, String message) {
    super(apiCode, message);
  }

  public ExistingUserException(ApiExceptionCode apiCode, String message, Throwable cause) {
    super(apiCode, message, cause);
  }
}
