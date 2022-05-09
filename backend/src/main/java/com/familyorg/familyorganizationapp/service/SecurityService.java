package com.familyorg.familyorganizationapp.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface SecurityService {
  boolean isAuthenticated();

  void autologin(String username, String password);

  UserDetails reauthenticate(String username, String password);
}
