package com.familyorg.familyorganizationapp.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends ApiException {
  private static final long serialVersionUID = -9094718249506978530L;

  public ResourceNotFoundException(ApiExceptionCode apiCode) {
    super(apiCode);
  }

  public ResourceNotFoundException(ApiExceptionCode apiCode, String message) {
    super(apiCode, message);
  }

  public ResourceNotFoundException(ApiExceptionCode apiCode, String message, Throwable cause) {
    super(apiCode, message, cause);
  }
}
