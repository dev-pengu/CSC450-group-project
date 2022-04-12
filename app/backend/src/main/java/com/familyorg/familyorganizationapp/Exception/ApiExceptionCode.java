package com.familyorg.familyorganizationapp.Exception;

public enum ApiExceptionCode {
  // Authentication related exceptions
  USER_NOT_LOGGED_IN(1000),
  USER_DOESNT_EXIST(1001),
  PASSWORD_MINIMUM_REQUIREMENTS_NOT_MET(1002),
  USERNAME_IN_USE(10003),
  EMAIL_IN_USE(1004),
  REAUTHENTICATION_NEEDED_FOR_REQUEST(1005),
  USER_NOT_IN_FAMILY(1006),
  USER_PRIVILEGES_TOO_LOW(1007),
  ACTION_NOT_PERMITTED(1008),

  // Request format related exceptions
  REQUIRED_PARAM_MISSING(2000),
  ERROR_PARSING_PARAM(2001),
  BAD_PARAM_VALUE(2002),
  ILLEGAL_ACTION_REQUESTED(2003),

  // Family related exceptions
  FAMILY_DOESNT_EXIST(3000),
  INVITE_CODE_DOESNT_EXIST(3001),
  CALENDAR_DOESNT_EXIST(3002),
  EVENT_DOESNT_EXIST(3003),
  LIST_DOESNT_EXIST(3004),
  LIST_ITEM_DOESNT_EXIST(3005);
  POLL_DOESNT_EXIST(3006),
  POLL_OPTION_DOESNT_EXIST(3007);

  private int code;

  ApiExceptionCode(int code) {
    this.code = code;
  }

  public int getCode() {
    return this.code;
  }
}
