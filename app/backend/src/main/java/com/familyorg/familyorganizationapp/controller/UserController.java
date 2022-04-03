package com.familyorg.familyorganizationapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.familyorg.familyorganizationapp.DTO.UserDto;
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
  public ResponseEntity<String> deleteUser(@RequestParam("username") String username) {
    userService.deleteUser(username);
    return new ResponseEntity<>("Success", HttpStatus.OK);
  }

  /**
   * Finds the user with username supplied.
   *
   * @return UserDto populated with user found
   */
  @GetMapping()
  public ResponseEntity<UserDto> getUser() {
    UserDto user = userService.getUserData();
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @GetMapping("/settings")
  public ResponseEntity<UserDto> getCurrentUserSettings() {
    UserDto response = userService.getSettingsForUser();
    return new ResponseEntity<UserDto>(response, HttpStatus.OK);
  }

  @PatchMapping("/settings")
  public ResponseEntity<UserDto> updateCurrentUserSettings(@RequestBody UserDto user) {
    UserDto response = userService.updateUserSettingsAndData(user);
    return new ResponseEntity<UserDto>(response, HttpStatus.OK);
  }
}
