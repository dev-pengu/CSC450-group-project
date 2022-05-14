package com.familyorg.familyorganizationapp.controller;

import com.familyorg.familyorganizationapp.Exception.ApiExceptionCode;
import com.familyorg.familyorganizationapp.Exception.AuthorizationException;
import com.familyorg.familyorganizationapp.Exception.BadRequestException;
import com.familyorg.familyorganizationapp.Exception.UserNotFoundException;
import com.familyorg.familyorganizationapp.service.AuthService;
import com.familyorg.familyorganizationapp.service.MessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.familyorg.familyorganizationapp.DTO.ErrorDto;
import com.familyorg.familyorganizationapp.DTO.UserDto;
import com.familyorg.familyorganizationapp.DTO.builder.ErrorDtoBuilder;
import com.familyorg.familyorganizationapp.domain.User;
import com.familyorg.familyorganizationapp.service.SecurityService;
import com.familyorg.familyorganizationapp.service.UserService;

@RestController
@RequestMapping("/api/services/auth")
public class AuthController {

  private UserService userService;
  private SecurityService securityService;
  private AuthService authService;
  private MessagingService messagingService;

  @Autowired
  public AuthController(UserService userService,
      SecurityService securityService,
      AuthService authService,
      MessagingService messagingService) {
    this.userService = userService;
    this.securityService = securityService;
    this.authService = authService;
    this.messagingService = messagingService;
  }

  @GetMapping("/csrf")
  public void getCsrf() {
  }

  /**
   * Create a new user. If username or email is already in use, will return HttpStatus 409 and
   * indication of which field is already in use.
   *
   * @param user
   * @return UserDto populated with currently logged in user
   */
  @PostMapping("/register")
  public ResponseEntity<?> saveUser(@RequestBody User user) {
    String originalPassword = user.getPassword();
    UserDto savedUser = userService.createUser(user);
    if (savedUser != null) {
      securityService.autologin(user.getUsername(), originalPassword);
      return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    } else {
      ErrorDto errorResponse =
          new ErrorDtoBuilder()
              .withErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
              .withMessage("Error processing request.")
              .build();
      return new ResponseEntity<ErrorDto>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Logs in the user and returns a UserDto populated with the currently logged in user on success.
   *
   * @param user
   * @return Currently logged in user on success.
   */
  @PostMapping("/login")
  public ResponseEntity<UserDto> login(@RequestBody User user) {
    User userObj;
    if (user.getUsername() == null && user.getEmail() != null) {
      userObj = userService.getUserByEmail(user.getEmail());
    } else {
      userObj = userService.getUserByUsername(user.getUsername());
    }
    if (userObj == null) {
      throw new UserNotFoundException(ApiExceptionCode.USER_DOESNT_EXIST,
          "User is not registered.");
    }
    securityService.autologin(userObj.getUsername(), user.getPassword());
    return new ResponseEntity<>(UserDto.fromUserObj(userObj), HttpStatus.OK);
  }

  @PostMapping("/changePassword")
  public ResponseEntity<String> changePassword(@RequestBody UserDto request) {
    if ((request.getUsername() == null || request.getUsername().isBlank())
        && (request.getEmail() == null || request.getEmail().isBlank())) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING,
          "Either a username or a password must be supplied.");
    }
    if (request.getResetCode() == null || request.getResetCode().isBlank()) {
      validateReauthenticatedResetRequest(request);
      User user = userService.getRequestingUser();
      UserDetails reauthenticatedUser =
          securityService.reauthenticate(request.getUsername(), request.getOldPassword());
      if (reauthenticatedUser == null
          || !user.getUsername().equals(reauthenticatedUser.getUsername())) {
        throw new AuthorizationException(
            ApiExceptionCode.ILLEGAL_ACTION_REQUESTED,
            "User is not authorized to complete this action");
      }
      userService.changePassword(user, request.getNewPassword());
    } else {
      validateCodeResetRequest(request);
      User user;
      if (request.getUsername() != null && !request.getUsername().isBlank()) {
        user = userService.getUserByUsername(request.getUsername());
      } else {
        user = userService.getUserByEmail(request.getEmail());
      }
      if (user == null) {
        throw new UserNotFoundException(ApiExceptionCode.USER_DOESNT_EXIST, "User not found.");
      }
      if (authService.verifyResetCode(request.getResetCode(), user)) {
        userService.changePassword(user, request.getNewPassword());
      } else {
        throw new AuthorizationException(
            ApiExceptionCode.ILLEGAL_ACTION_REQUESTED,
            "User is not permitted to perform this action.");
      }
    }

    return new ResponseEntity<>("Success", HttpStatus.OK);
  }

  @PostMapping("/passwordReset")
  public ResponseEntity<String> requestPasswordReset(@RequestBody UserDto request) {
    if ((request.getUsername() == null || request.getUsername().isBlank())
        && (request.getEmail() == null || request.getEmail().isBlank())) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING,
          "Either a username or a password must be supplied.");
    }
    User user;
    if (request.getUsername() != null && !request.getUsername().isBlank()) {
      user = userService.getUserByUsername(request.getUsername());
    } else {
      user = userService.getUserByEmail(request.getEmail());
    }
    String emailContents =
        messagingService.buildPasswordResetContent(authService.generateResetCode(user));
    messagingService.sendHtmlEmail(
        user.getEmail(), "Password Reset for Family Command Center", emailContents);
    return new ResponseEntity<>("Success", HttpStatus.OK);
  }

  @GetMapping("/usernameCheck")
  public ResponseEntity<Boolean> checkUsername(@RequestParam() String username) {
    User user = userService.getUserByUsername(username);
    if (user == null) {
      return new ResponseEntity<>(true, HttpStatus.OK);
    }
    return new ResponseEntity<>(false, HttpStatus.OK);
  }

  @GetMapping("/emailCheck")
  public ResponseEntity<Boolean> checkEmail(@RequestParam() String email) {
    User user = userService.getUserByEmail(email);
    if (user == null) {
      return new ResponseEntity<>(true, HttpStatus.OK);
    }
    return new ResponseEntity<>(false, HttpStatus.OK);
  }

  private void validateReauthenticatedResetRequest(UserDto request) {
    if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING,
          "A new password is required to change password.");
    }
    if (request.getOldPassword() == null || request.getOldPassword().isBlank()) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING,
          "Old password is required to change password.");
    }
    if (!authService.verifyPasswordRequirements(request.getNewPassword())) {
      throw new BadRequestException(
          ApiExceptionCode.PASSWORD_MINIMUM_REQUIREMENTS_NOT_MET,
          "Password does not meet minimum requirements");
    }
  }

  private void validateCodeResetRequest(UserDto request) {
    if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING,
          "A new password is required to change password.");
    }
    if (!authService.verifyPasswordRequirements(request.getNewPassword())) {
      throw new BadRequestException(
          ApiExceptionCode.PASSWORD_MINIMUM_REQUIREMENTS_NOT_MET,
          "Password does not meet minimum requirements");
    }
  }
}
