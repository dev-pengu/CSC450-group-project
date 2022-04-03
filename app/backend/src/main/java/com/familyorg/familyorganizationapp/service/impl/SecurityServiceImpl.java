package com.familyorg.familyorganizationapp.service.impl;

import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import com.familyorg.familyorganizationapp.repository.UserRepository;
import com.familyorg.familyorganizationapp.service.SecurityService;

@Service
public class SecurityServiceImpl implements SecurityService {
  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private UserDetailsService userDetailsService;
  @Autowired
  private UserRepository userRepository;

  private Logger LOG = LoggerFactory.getLogger(SecurityServiceImpl.class);

  @Override
  @Transactional
  public void autologin(String username, String password) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
        new UsernamePasswordAuthenticationToken(userDetails, password,
            userDetails.getAuthorities());

    authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    if (usernamePasswordAuthenticationToken.isAuthenticated()) {
      userRepository.updateLastLoggedIn(username);
      SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }
  }

  @Override
  public boolean isAuthenticated() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      LOG.error(authentication.toString());
    }
    if (authentication == null
        || AnonymousAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
      return false;
    }

    return authentication.isAuthenticated();
  }

  @Override
  @Transactional
  public UserDetails reauthenticate(String username, String password) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
        new UsernamePasswordAuthenticationToken(userDetails, password,
            userDetails.getAuthorities());

    authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    if (usernamePasswordAuthenticationToken.isAuthenticated()) {
      userRepository.updateLastLoggedIn(username);
      return userDetails;
    }
    return null;
  }
}
