package com.familyorg.familyorganizationapp.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends ApiException {

  private static final long serialVersionUID = 1794713462340008397L;

  public BadRequestException(ApiExceptionCode apiCode) {
    super(apiCode);
  }

  public BadRequestException(ApiExceptionCode apiCode, String message) {
    super(apiCode, message);
  }

  public BadRequestException(ApiExceptionCode apiCode, String message, Throwable cause) {
    super(apiCode, message, cause);
  }
}
