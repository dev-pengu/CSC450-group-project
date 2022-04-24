package com.familyorg.familyorganizationapp.service;

import com.familyorg.familyorganizationapp.domain.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService extends UserDetailsService {
  UserDetails getSessionUserDetails();

  boolean verifyPasswordRequirements(String password);

  boolean hasAuthenticatedForSensitiveActions(String username);

  String generateResetCode(User user);

  boolean verifyResetCode(String resetCode, User user);
}
