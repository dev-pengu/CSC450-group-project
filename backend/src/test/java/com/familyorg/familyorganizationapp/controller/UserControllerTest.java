package com.familyorg.familyorganizationapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.familyorg.familyorganizationapp.DTO.UserDto;
import com.familyorg.familyorganizationapp.DTO.builder.UserDtoBuilder;
import com.familyorg.familyorganizationapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

public class UserControllerTest {

  private UserService userService;
  private UserController userController;

  @BeforeEach
  public void setup() {
    userService = mock(UserService.class);
    userController = new UserController(userService);
  }

  @Test
  public void deleteUser_success() {
    /* Given */
    doNothing().when(userService).deleteUser(any());

    /* When */
    ResponseEntity<String> response = userController.deleteUser("testuser");

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_deleteUser_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    doThrow(RuntimeException.class).when(userService).deleteUser(any());

    /* When */
    assertThrows(
        RuntimeException.class,
        () -> {
          userController.deleteUser("testuser");
        });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void getUser_success() {
    /* Given */
    when(userService.getUserData()).thenReturn(new UserDtoBuilder().build());

    /* When */
    ResponseEntity<UserDto> response = userController.getUser();

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_getUser_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    when(userService.getUserData()).thenThrow(RuntimeException.class);

    /* When */
    assertThrows(
        RuntimeException.class,
        () -> {
          userController.getUser();
        });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void getCurrentUserSettings_success() {
    /* Given */
    when(userService.getSettingsForUser()).thenReturn(new UserDtoBuilder().build());

    /* When */
    ResponseEntity<UserDto> response = userController.getCurrentUserSettings();

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_getCurrentUserSettings_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    when(userService.getSettingsForUser()).thenThrow(RuntimeException.class);

    /* When */
    assertThrows(
        RuntimeException.class,
        () -> {
          userController.getCurrentUserSettings();
        });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void updateCurrentUserSettings_success() {
    /* Given */
    when(userService.updateUserSettingsAndData(any())).thenReturn(new UserDtoBuilder().build());

    /* When */
    ResponseEntity<UserDto> response =
        userController.updateCurrentUserSettings(new UserDtoBuilder().build());

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_updateCurrentUserSettings_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    when(userService.updateUserSettingsAndData(any())).thenThrow(RuntimeException.class);

    /* When */
    assertThrows(
        RuntimeException.class,
        () -> {
          userController.updateCurrentUserSettings(new UserDtoBuilder().build());
        });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }
}
