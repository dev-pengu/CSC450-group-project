package com.familyorg.familyorganizationapp.service.impl;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.familyorg.familyorganizationapp.Exception.UserNotFoundException;
import com.familyorg.familyorganizationapp.domain.User;
import com.familyorg.familyorganizationapp.repository.UserRepository;
import com.familyorg.familyorganizationapp.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

  @Autowired
  UserRepository userRepository;

  private final Pattern passwordPattern =
      Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()-+?]).+$");

  @Override
  public UserDetails loadUserByUsername(String username) throws UserNotFoundException {
    User user = userRepository.findByUsername(username);
    if (user == null) {
      throw new UserNotFoundException("Username not found");
    }
    List<GrantedAuthority> auth = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");
    String password = user.getPassword();
    return new org.springframework.security.core.userdetails.User(username, password, auth);
  }

  @Override
  public UserDetails getSessionUserDetails() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null ? (UserDetails) authentication.getPrincipal() : null;
  }

  /**
   * Password must contain at least: 1 uppercase, 1 lowercase, 1 number, 1 special character, and
   * must be at least 12 characters long. Returns true if the password meets the minimum
   * requirements
   */
  @Override
  public boolean verifyPasswordRequirements(String password) {
    if (password.length() < 12) {
      return false;
    }
    return passwordPattern.matcher(password).matches();
  }

  @Override
  public boolean hasAuthenticatedForSensitiveActions(String username) {
    User user = userRepository.findByUsername(username);
    Calendar cal = Calendar.getInstance();
    cal.setTime(Date.from(Instant.now()));
    cal.add(Calendar.MINUTE, -15);
    if (user.getLastLoggedIn().compareTo(cal.getTime()) > 0) {
      return true;
    }
    return false;
  }
}
