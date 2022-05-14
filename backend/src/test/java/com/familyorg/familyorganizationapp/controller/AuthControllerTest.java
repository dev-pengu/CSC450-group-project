package com.familyorg.familyorganizationapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.familyorg.familyorganizationapp.DTO.ErrorDto;
import com.familyorg.familyorganizationapp.DTO.UserDto;
import com.familyorg.familyorganizationapp.DTO.builder.UserDtoBuilder;
import com.familyorg.familyorganizationapp.Exception.AuthorizationException;
import com.familyorg.familyorganizationapp.Exception.BadRequestException;
import com.familyorg.familyorganizationapp.Exception.UserNotFoundException;
import com.familyorg.familyorganizationapp.domain.User;
import com.familyorg.familyorganizationapp.service.AuthService;
import com.familyorg.familyorganizationapp.service.MessagingService;
import com.familyorg.familyorganizationapp.service.SecurityService;
import com.familyorg.familyorganizationapp.service.UserService;
import java.util.Collection;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthControllerTest {
  private UserService userService;
  private SecurityService securityService;
  private AuthService authService;
  private MessagingService messagingService;
  private AuthController authController;

  @BeforeEach
  public void setup() {
    userService = mock(UserService.class);
    securityService = mock(SecurityService.class);
    authService = mock(AuthService.class);
    messagingService = mock(MessagingService.class);
    authController =
        new AuthController(userService, securityService, authService, messagingService);
  }

  @Test
  public void saveUser_success() {
    /* Given */
    when(userService.createUser(any(User.class)))
        .thenReturn(new UserDtoBuilder().withId(1L).build());
    doNothing().when(securityService).autologin(any(String.class), any(String.class));
    User request = new User();
    request.setFirstName("Test");
    request.setLastName("User");
    request.setEmail("testuser@test.com");
    request.setUsername("testuser");
    request.setPassword("password");

    /* When */
    ResponseEntity<?> response = authController.saveUser(request);

    /* Then */
    assertNotNull(response);
    assertEquals(201, response.getStatusCodeValue());
    assertTrue(response.getBody() instanceof UserDto);
  }

  @Test
  public void saveUser_null_user_returned() {
    /* Given */
    when(userService.createUser(any(User.class))).thenReturn(null);
    doNothing().when(securityService).autologin(any(String.class), any(String.class));
    User request = new User();
    request.setFirstName("Test");
    request.setLastName("User");
    request.setEmail("testuser@test.com");
    request.setUsername("testuser");
    request.setPassword("password");

    /* When */
    ResponseEntity<?> response = authController.saveUser(request);

    /* Then */
    assertNotNull(response);
    assertEquals(500, response.getStatusCodeValue());
    assertTrue(response.getBody() instanceof ErrorDto);
  }

  @Test
  public void login_with_username_success() {
    /* Given */
    when(userService.getUserByUsername(any(String.class)))
        .thenReturn(
            new User(1L, "Test", "User", "testuser", "password", "testuser@test.com", null));
    doNothing().when(securityService).autologin(any(String.class), any(String.class));
    User request = new User();
    request.setUsername("testuser");
    request.setPassword("password");

    /* When */
    ResponseEntity<?> response = authController.login(request);

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertTrue(response.getBody() instanceof UserDto);
  }

  @Test
  public void login_with_email_success() {
    /* Given */
    when(userService.getUserByEmail(any(String.class)))
        .thenReturn(
            new User(1L, "Test", "User", "testuser", "password", "testuser@test.com", null));
    doNothing().when(securityService).autologin(any(String.class), any(String.class));
    User request = new User();
    request.setEmail("testuser@test.com");
    request.setPassword("password");

    /* When */
    ResponseEntity<?> response = authController.login(request);

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertTrue(response.getBody() instanceof UserDto);
  }

  @Test
  public void when_login_and_user_not_found_then_404_returned() {
    /* Given */
    when(userService.getUserByUsername(any(String.class))).thenReturn(null);
    User request = new User();
    request.setEmail("testuser@test.com");
    request.setPassword("password");

    /* When */
    assertThrows(
        UserNotFoundException.class,
        () -> {
          authController.login(request);
        });

    /* Then */
    // nothing, the call should have failed with an error
  }

  @Test
  public void changePassword_success_without_code() {
    when(userService.getRequestingUser())
        .thenReturn(
            new User(1L, "Test", "User", "testuser", "password", "testuser@test.com", null));
    when(securityService.reauthenticate(any(String.class), any(String.class)))
        .thenReturn(
            new UserDetails() {
              @Override
              public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
              }

              @Override
              public String getPassword() {
                return "password";
              }

              @Override
              public String getUsername() {
                return "testuser";
              }

              @Override
              public boolean isAccountNonExpired() {
                return false;
              }

              @Override
              public boolean isAccountNonLocked() {
                return false;
              }

              @Override
              public boolean isCredentialsNonExpired() {
                return false;
              }

              @Override
              public boolean isEnabled() {
                return false;
              }
            });
    doNothing().when(userService).changePassword(any(User.class), any(String.class));
    when(authService.verifyPasswordRequirements(any(String.class))).thenReturn(true);
    UserDto request =
        new UserDto(
            null, null, null, null, "testuser", "password", "newpassword", null, null, null, null);

    /* When */
    ResponseEntity<?> response = authController.changePassword(request);

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertTrue(response.getBody() instanceof String);
  }

  @Test
  public void
      when_changePassword_and_username_supplied_is_not_same_as_requesting_user_then_authorization_exception_thrown() {
    when(userService.getRequestingUser())
        .thenReturn(
            new User(1L, "Test", "User", "testuser", "password", "testuser@test.com", null));
    when(securityService.reauthenticate(any(String.class), any(String.class)))
        .thenReturn(
            new UserDetails() {
              @Override
              public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
              }

              @Override
              public String getPassword() {
                return "password";
              }

              @Override
              public String getUsername() {
                return "testuser2";
              }

              @Override
              public boolean isAccountNonExpired() {
                return false;
              }

              @Override
              public boolean isAccountNonLocked() {
                return false;
              }

              @Override
              public boolean isCredentialsNonExpired() {
                return false;
              }

              @Override
              public boolean isEnabled() {
                return false;
              }
            });
    doNothing().when(userService).changePassword(any(User.class), any(String.class));
    when(authService.verifyPasswordRequirements(any(String.class))).thenReturn(true);
    UserDto request =
        new UserDto(
            null, null, null, null, "testuser", "password", "newpassword", null, null, null, null);

    /* When */
    assertThrows(
        AuthorizationException.class,
        () -> {
          authController.changePassword(request);
        });

    /* Then */
    // nothing, the service call should have failed with an exception
  }

  @Test
  public void when_changePassword_with_reset_code_success() {
    /* Given */
    doNothing().when(userService).changePassword(any(User.class), any(String.class));
    UserDto request =
        new UserDto(
            null,
            null,
            null,
            null,
            "testuser",
            null,
            "newpassword",
            null,
            null,
            null,
            UUID.randomUUID().toString());
    when(authService.verifyPasswordRequirements(any(String.class))).thenReturn(true);
    when(userService.getUserByUsername(any(String.class)))
        .thenReturn(
            new User(1L, "Test", "User", "testuser", "password", "testuser@test.com", null));
    when(authService.verifyResetCode(any(String.class), any(User.class))).thenReturn(true);

    /* When */
    ResponseEntity<?> response = authController.changePassword(request);

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertTrue(response.getBody() instanceof String);
  }

  @Test
  public void when_changePassword_with_reset_code_and_invalid_request_then_bad_reqest_thrown() {
    /* Given */
    doNothing().when(userService).changePassword(any(User.class), any(String.class));
    UserDto request =
        new UserDto(
            null,
            null,
            null,
            null,
            "testuser",
            null,
            "newpassword",
            null,
            null,
            null,
            UUID.randomUUID().toString());
    when(authService.verifyPasswordRequirements(any(String.class))).thenReturn(false);
    when(userService.getUserByUsername(any(String.class)))
        .thenReturn(
            new User(1L, "Test", "User", "testuser", "password", "testuser@test.com", null));
    when(authService.verifyResetCode(any(String.class), any(User.class))).thenReturn(true);

    /* When */
    assertThrows(
        BadRequestException.class,
        () -> {
          authController.changePassword(request);
        });

    /* Then */
    // nothing
  }

  @Test
  public void
      when_change_password_with_reset_code_and_invalid_code_then_authorization_exception_thrown() {
    /* Given */
    doNothing().when(userService).changePassword(any(User.class), any(String.class));
    UserDto request =
        new UserDto(
            null,
            null,
            null,
            null,
            "testuser",
            null,
            "newpassword",
            null,
            null,
            null,
            UUID.randomUUID().toString());
    when(authService.verifyPasswordRequirements(any(String.class))).thenReturn(true);
    when(userService.getUserByUsername(any(String.class)))
        .thenReturn(
            new User(1L, "Test", "User", "testuser", "password", "testuser@test.com", null));
    when(authService.verifyResetCode(any(String.class), any(User.class))).thenReturn(false);

    /* When */
    assertThrows(
        AuthorizationException.class,
        () -> {
          authController.changePassword(request);
        });

    /* Then */
    // nothing
  }

  @Test
  public void when_changePassword_with_reset_code_and_user_not_found_then_user_not_found_thrown() {
    /* Given */
    doNothing().when(userService).changePassword(any(User.class), any(String.class));
    UserDto request =
        new UserDto(
            null,
            null,
            null,
            null,
            "testuser",
            null,
            "newpassword",
            null,
            null,
            null,
            UUID.randomUUID().toString());
    when(authService.verifyPasswordRequirements(any(String.class))).thenReturn(true);
    when(userService.getUserByUsername(any(String.class))).thenReturn(null);
    when(authService.verifyResetCode(any(String.class), any(User.class))).thenReturn(true);

    /* When */
    assertThrows(
        UserNotFoundException.class,
        () -> {
          authController.changePassword(request);
        });

    /* Then */
    // nothing
  }

  @Test
  public void requestPasswordReset_with_username_success() {
    /* Given */
    when(userService.getUserByUsername(any(String.class)))
        .thenReturn(
            new User(1L, "Test", "User", "testuser", "password", "testuser@test.com", null));
    when(messagingService.buildPasswordResetContent(any(String.class))).thenReturn("");
    doNothing()
        .when(messagingService)
        .sendHtmlEmail(any(String.class), any(String.class), any(String.class));
    when(authService.generateResetCode(any(User.class))).thenReturn("");
    UserDto request = new UserDtoBuilder().withUsername("testuser").build();

    /* When */
    ResponseEntity<?> response = authController.requestPasswordReset(request);

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertTrue(response.getBody() instanceof String);
  }

  @Test
  public void requestPasswordReset_with_email_success() {
    /* Given */
    when(userService.getUserByEmail(any(String.class)))
      .thenReturn(
        new User(1L, "Test", "User", "testuser", "password", "testuser@test.com", null));
    when(messagingService.buildPasswordResetContent(any(String.class))).thenReturn("");
    doNothing()
      .when(messagingService)
      .sendHtmlEmail(any(String.class), any(String.class), any(String.class));
    when(authService.generateResetCode(any(User.class))).thenReturn("");
    UserDto request = new UserDtoBuilder().withEmail("testuser@test.com").build();

    /* When */
    ResponseEntity<?> response = authController.requestPasswordReset(request);

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertTrue(response.getBody() instanceof String);
  }

  @Test
  public void when_requestPasswordReset_and_request_missing_email_andusername_then_bad_request_thrown() {
    /* Given */
    when(userService.getUserByEmail(any(String.class)))
      .thenReturn(
        new User(1L, "Test", "User", "testuser", "password", "testuser@test.com", null));
    when(messagingService.buildPasswordResetContent(any(String.class))).thenReturn("");
    doNothing()
      .when(messagingService)
      .sendHtmlEmail(any(String.class), any(String.class), any(String.class));
    when(authService.generateResetCode(any(User.class))).thenReturn("");
    UserDto request = new UserDtoBuilder().build();

    /* When */
    assertThrows(BadRequestException.class, () -> {
      authController.requestPasswordReset(request);
    });

    /* Then */
    // nothing
  }
}
