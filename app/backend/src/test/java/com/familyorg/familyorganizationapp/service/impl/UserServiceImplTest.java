package com.familyorg.familyorganizationapp.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Collection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.familyorg.familyorganizationapp.DTO.UserDto;
import com.familyorg.familyorganizationapp.Exception.AuthorizationException;
import com.familyorg.familyorganizationapp.Exception.UserNotFoundException;
import com.familyorg.familyorganizationapp.domain.User;
import com.familyorg.familyorganizationapp.repository.UserRepository;
import com.familyorg.familyorganizationapp.service.AuthService;
import com.familyorg.familyorganizationapp.service.SecurityService;

public class UserServiceImplTest {
  private UserServiceImpl userService;

  private UserRepository userRepository;
  private AuthService authService;
  private SecurityService securityService;
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  static User TEST_USER_1 =
      new User(1l, "Test", "User", "testuser", "password", "testuser@test.com", null);

  @BeforeAll
  public static void setup() {

  }

  @BeforeEach
  public void before() {
    authService = mock(AuthService.class);
    userRepository = mock(UserRepository.class);
    securityService = mock(SecurityService.class);
    bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    userService =
        new UserServiceImpl(userRepository, authService, securityService, bCryptPasswordEncoder);
  }

  @Test
  public void when_get_user_then_user_info_returned() {
    /* Given */
    when(authService.getSessionUserDetails()).thenReturn(new UserDetails() {
      @Override
      public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
      }

      @Override
      public String getPassword() {
        return TEST_USER_1.getPassword();
      }

      @Override
      public String getUsername() {
        return TEST_USER_1.getUsername();
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
    when(userRepository.findByUsername(TEST_USER_1.getUsername())).thenReturn(TEST_USER_1);

    /* When */
    UserDto response = userService.getUserData();

    /* Then */
    assertNotNull(response);

  }

  @Test
  public void when_get_user_and_no_authenticated_user_found_then_authorization_exception_thrown() {
    /* Given */
    when(authService.getSessionUserDetails()).thenReturn(new UserDetails() {
      @Override
      public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
      }

      @Override
      public String getPassword() {
        return null;
      }

      @Override
      public String getUsername() {
        return null;
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
    when(userRepository.findByUsername(TEST_USER_1.getUsername())).thenReturn(TEST_USER_1);

    /* When */
    assertThrows(AuthorizationException.class, () -> {
      userService.getUserData();
    });
  }

  @Test
  public void when_get_user_and_user_doesnt_exist_then_user_not_found_exception_thrown() {
    /* Given */
    when(authService.getSessionUserDetails()).thenReturn(new UserDetails() {
      @Override
      public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
      }

      @Override
      public String getPassword() {
        return TEST_USER_1.getPassword();
      }

      @Override
      public String getUsername() {
        return TEST_USER_1.getUsername();
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
    when(userRepository.findByUsername(TEST_USER_1.getUsername())).thenReturn(null);

    /* Then */
    assertThrows(UserNotFoundException.class, () -> {
      userService.getUserData();
    });
  }

  @Test
  public void user_deleted_successfully() {
    /* Given */
    when(authService.getSessionUserDetails()).thenReturn(new UserDetails() {
      @Override
      public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
      }

      @Override
      public String getPassword() {
        return TEST_USER_1.getPassword();
      }

      @Override
      public String getUsername() {
        return TEST_USER_1.getUsername();
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
    when(userRepository.findByUsername(TEST_USER_1.getUsername())).thenReturn(TEST_USER_1);
    doNothing().when(userRepository).delete(TEST_USER_1);

    /* When */
    userService.deleteUser(TEST_USER_1.getUsername());

    /* Then */
    // As long as no errors are thrown everything is good
  }

  @Test
  public void when_delete_user_and_no_authenticated_user_found_then_authorization_exception_thrown() {
    /* Given */
    when(authService.getSessionUserDetails()).thenReturn(new UserDetails() {
      @Override
      public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
      }

      @Override
      public String getPassword() {
        return null;
      }

      @Override
      public String getUsername() {
        return null;
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
    when(userRepository.findByUsername(TEST_USER_1.getUsername())).thenReturn(TEST_USER_1);
    doNothing().when(userRepository).delete(TEST_USER_1);

    /* When */
    assertThrows(AuthorizationException.class, () -> {
      userService.deleteUser(TEST_USER_1.getUsername());
    });
  }

  @Test
  public void when_delete_user_and_logged_in_user_doesnt_match_then_authorization_exception_thrown() {
    /* Given */
    when(authService.getSessionUserDetails()).thenReturn(new UserDetails() {
      @Override
      public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
      }

      @Override
      public String getPassword() {
        return TEST_USER_1.getPassword();
      }

      @Override
      public String getUsername() {
        return TEST_USER_1.getUsername();
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
    when(userRepository.findByUsername(TEST_USER_1.getUsername())).thenReturn(TEST_USER_1);
    doNothing().when(userRepository).delete(TEST_USER_1);

    /* When */
    assertThrows(AuthorizationException.class, () -> {
      userService.deleteUser("userthatdoesntexist");
    });
  }

  @Test
  public void when_delete_user_and_user_doesnt_exist_then_user_not_found_exception_thrown() {
    /* Given */
    when(authService.getSessionUserDetails()).thenReturn(new UserDetails() {
      @Override
      public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
      }

      @Override
      public String getPassword() {
        return TEST_USER_1.getPassword();
      }

      @Override
      public String getUsername() {
        return TEST_USER_1.getUsername();
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
    when(userRepository.findByUsername(TEST_USER_1.getUsername())).thenReturn(null);
    doNothing().when(userRepository).delete(TEST_USER_1);

    /* When */
    assertThrows(UserNotFoundException.class, () -> {
      userService.deleteUser(TEST_USER_1.getUsername());
    });
  }
}
