package com.familyorg.familyorganizationapp.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService extends UserDetailsService {
  UserDetails getSessionUserDetails();

  boolean verifyPasswordRequirements(String password);
}
