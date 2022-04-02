package com.familyorg.familyorganizationapp.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.familyorg.familyorganizationapp.DTO.ErrorDto;
import com.familyorg.familyorganizationapp.DTO.builder.ErrorDtoBuilder;
import com.familyorg.familyorganizationapp.Exception.AuthorizationException;
import com.familyorg.familyorganizationapp.Exception.BadRequestException;
import com.familyorg.familyorganizationapp.Exception.ExistingUserException;
import com.familyorg.familyorganizationapp.Exception.IncorrectCredentialsException;
import com.familyorg.familyorganizationapp.Exception.ResourceNotFoundException;
import com.familyorg.familyorganizationapp.Exception.UserNotFoundException;

@ControllerAdvice
public class CustomControllerAdvice {

  private static Logger logger = LoggerFactory.getLogger(CustomControllerAdvice.class);

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorDto> handleResourceNotFound(Exception e) {
    ErrorDto errorResponse = new ErrorDtoBuilder().withErrorCode(HttpStatus.NOT_FOUND.value())
        .withMessage(e.getMessage()).build();
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorDto> handleUserNotFound(Exception e) {
    ErrorDto errorResponse = new ErrorDtoBuilder().withErrorCode(HttpStatus.NOT_FOUND.value())
        .withMessage(e.getMessage()).addRedirect("/register").build();
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ErrorDto> handleBadRequest(Exception e) {
    ErrorDto errorResponse = new ErrorDtoBuilder().withErrorCode(HttpStatus.BAD_REQUEST.value())
        .withMessage(e.getMessage()).build();
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ExistingUserException.class)
  public ResponseEntity<ErrorDto> handleExistingUser(Exception e) {
    ErrorDto errorResponse = new ErrorDtoBuilder().withErrorCode(HttpStatus.CONFLICT.value())
        .withMessage(e.getMessage()).build();
    return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(IncorrectCredentialsException.class)
  public ResponseEntity<ErrorDto> handleIncorrectCredentials(Exception e) {
    ErrorDto errorResponse = new ErrorDtoBuilder().withErrorCode(HttpStatus.UNAUTHORIZED.value())
        .withMessage(e.getMessage()).build();
    return new ResponseEntity<ErrorDto>(errorResponse, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorDto> handleBadCredentials(Exception e) {
    ErrorDto errorResponse = new ErrorDtoBuilder().withErrorCode(HttpStatus.UNAUTHORIZED.value())
        .withMessage(e.getMessage()).build();
    return new ResponseEntity<ErrorDto>(errorResponse, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(AuthorizationException.class)
  public ResponseEntity<ErrorDto> handleUnauthorized(Exception e) {
    ErrorDtoBuilder errorResponseBuilder = new ErrorDtoBuilder()
        .withErrorCode(HttpStatus.UNAUTHORIZED.value()).withMessage(e.getMessage());
    if (((AuthorizationException) e).isRedirect()) {
      errorResponseBuilder.addRedirect("/login");
    }
    return new ResponseEntity<>(errorResponseBuilder.build(), HttpStatus.UNAUTHORIZED);
  }


  // fallback method
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorDto> handleExceptions(Exception e) {
    logger.error(e.getMessage(), e);

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    e.printStackTrace(printWriter);
    String stackTrace = stringWriter.toString();

    ErrorDto errorResponse =
        new ErrorDtoBuilder().withErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .withMessage(e.getMessage()).build();
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
