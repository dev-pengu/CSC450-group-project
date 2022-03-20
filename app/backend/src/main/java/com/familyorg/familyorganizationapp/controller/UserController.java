package com.familyorg.familyorganizationapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.familyorg.familyorganizationapp.DTO.ErrorDto;
import com.familyorg.familyorganizationapp.DTO.UserDto;
import com.familyorg.familyorganizationapp.DTO.builder.ErrorDtoBuilder;
import com.familyorg.familyorganizationapp.Exception.AuthorizationException;
import com.familyorg.familyorganizationapp.Exception.UserNotFoundException;
import com.familyorg.familyorganizationapp.service.UserService;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

  private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

  @Autowired
  private UserService userService;

  /**
   * Deletes the user with the supplied username.
   *
   * @param username
   * @return
   */
  @DeleteMapping()
  public ResponseEntity<?> deleteUser(@RequestParam("username") String username) {
    try {
      userService.deleteUser(username);
      return new ResponseEntity<>("Success", HttpStatus.OK);
    } catch (UserNotFoundException e) {
      LOG.error(e.getMessage(), e);
      ErrorDto errorResponse = new ErrorDtoBuilder().withErrorCode(HttpStatus.NOT_FOUND.value())
          .withMessage(e.getMessage()).addRedirect("/register").build();
      return new ResponseEntity<ErrorDto>(errorResponse, HttpStatus.NOT_FOUND);
    } catch (AuthorizationException e) {
      LOG.error(e.getMessage(), e);
      ErrorDtoBuilder errorResponse = new ErrorDtoBuilder()
          .withErrorCode(HttpStatus.UNAUTHORIZED.value()).withMessage(e.getMessage());
      if (e.isRedirect()) {
        errorResponse = errorResponse.addRedirect("/login");
      }
      return new ResponseEntity<ErrorDto>(errorResponse.build(), HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      ErrorDto errorResponse =
          new ErrorDtoBuilder().withErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
              .withMessage("Error processing request.").build();
      return new ResponseEntity<ErrorDto>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Finds the user with username supplied.
   *
   * @return UserDto populated with user found
   */
  @GetMapping()
  public ResponseEntity<?> getUser() {
    try {
      UserDto user = userService.getUserData();
      return new ResponseEntity<>(user, HttpStatus.OK);
    } catch (UserNotFoundException e) {
      LOG.error(e.getMessage(), e);
      ErrorDto errorResponse = new ErrorDtoBuilder().withErrorCode(HttpStatus.NOT_FOUND.value())
          .withMessage(e.getMessage()).addRedirect("/register").build();
      return new ResponseEntity<ErrorDto>(errorResponse, HttpStatus.NOT_FOUND);
    } catch (AuthorizationException e) {
      LOG.error(e.getMessage(), e);
      ErrorDtoBuilder errorResponse = new ErrorDtoBuilder()
          .withErrorCode(HttpStatus.UNAUTHORIZED.value()).withMessage(e.getMessage());
      if (e.isRedirect()) {
        errorResponse = errorResponse.addRedirect("/login");
      }
      return new ResponseEntity<ErrorDto>(errorResponse.build(), HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      ErrorDto errorResponse =
          new ErrorDtoBuilder().withErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
              .withMessage("Error processing request.").build();
      return new ResponseEntity<ErrorDto>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}
