package com.familyorg.familyorganizationapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
  @Autowired
  private UserService userService;
  @Autowired
  private SecurityService securityService;

  private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);


  /**
   * Create a new user. If username or email is already in use, will return HttpStatus 409 and
   * indication of which field is already in use.
   *
   * @param user
   * @return UserDto populated with currently logged in user
   */
  @PostMapping()
  public ResponseEntity<?> saveUser(@RequestBody User user) {
    String originalPassword = user.getPassword();
    UserDto savedUser = userService.createUser(user);
    if (savedUser != null) {
      securityService.autologin(user.getUsername(), originalPassword);
      return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    } else {
      ErrorDto errorResponse =
          new ErrorDtoBuilder().withErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
              .withMessage("Error processing request.").build();
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
  public ResponseEntity<?> login(@RequestBody User user) {
    securityService.autologin(user.getUsername(), user.getPassword());
    User loggedInUser = userService.getUserByUsername(user.getUsername());
    return new ResponseEntity<>(UserDto.fromUserObj(loggedInUser), HttpStatus.OK);
  }

  @PatchMapping("/change-password")
  public ResponseEntity<?> changePassword(@RequestBody UserDto user) {
    userService.changePassword(user);
    return new ResponseEntity<String>("Success", HttpStatus.OK);
  }
}
