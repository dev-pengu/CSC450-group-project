package com.familyorg.familyorganizationapp.DTO;

import java.util.Objects;

public class ErrorDto {
  private final String message;
  private final int errorCode;
  private final boolean redirect;
  private final String redirectUrl;

  public ErrorDto(String message, int errorCode, boolean redirect, String redirectUrl) {
    this.message = message;
    this.errorCode = errorCode;
    this.redirect = redirect;
    this.redirectUrl = redirectUrl;
  }

  public String getMessage() {
    return message;
  }

  public int getErrorCode() {
    return errorCode;
  }

  public boolean isRedirect() {
    return redirect;
  }

  public String getRedirectUrl() {
    return redirectUrl;
  }

  @Override
  public int hashCode() {
    return Objects.hash(errorCode, message, redirect, redirectUrl);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ErrorDto other = (ErrorDto) obj;
    return Objects.equals(errorCode, other.errorCode) && Objects.equals(message, other.message)
        && redirect == other.redirect && Objects.equals(redirectUrl, other.redirectUrl);
  }

  @Override
  public String toString() {
    return "ErrorDto [message=" + message + ", errorCode=" + errorCode + ", redirect=" + redirect
        + ", redirectUrl=" + redirectUrl + "]";
  }


}
