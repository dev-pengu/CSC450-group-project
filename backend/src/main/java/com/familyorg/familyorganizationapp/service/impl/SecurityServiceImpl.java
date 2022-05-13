package com.familyorg.familyorganizationapp.service.impl;

import com.familyorg.familyorganizationapp.Exception.ApiException;
import com.familyorg.familyorganizationapp.Exception.ApiExceptionCode;
import com.familyorg.familyorganizationapp.Exception.AuthorizationException;
import com.familyorg.familyorganizationapp.Exception.BadRequestException;
import com.familyorg.familyorganizationapp.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;
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

  @Value("${auth.login.attempts.max:3}")
  private int maxLoginAttempts;

  private final Logger logger = LoggerFactory.getLogger(SecurityServiceImpl.class);

  @Override
  @Transactional(noRollbackFor = {BadCredentialsException.class, ApiException.class})
  public void autologin(String username, String password) {
    if (username == null || username.isBlank()) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Username cannot be null.");
    }
    if (password == null || password.isBlank()) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Password cannot be null.");
    }
    User user = userRepository.findByUsernameIgnoreCase(username);
    if (user != null) {
      if (user.getLoginAttempts() == maxLoginAttempts) {
        user.setLocked(true);
        userRepository.save(user);
        throw new AuthorizationException(ApiExceptionCode.ACCOUNT_LOCKED,
            "Your account has been locked as a result of too many failed attempts.");
      } else if (user.isLocked()) {
        throw new AuthorizationException(ApiExceptionCode.ACCOUNT_LOCKED,
            "Your account has been locked as a result of too many failed attempts.");
      } else {
        user.setLoginAttempts(user.getLoginAttempts() + 1);
        userRepository.save(user);
      }
    }
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
        new UsernamePasswordAuthenticationToken(
            userDetails, password, userDetails.getAuthorities());

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
      logger.error(authentication.toString());
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
    if (username == null || username.isBlank()) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Username cannot be null.");
    }
    if (password == null || password.isBlank()) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Password cannot be null.");
    }
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
        new UsernamePasswordAuthenticationToken(
            userDetails, password, userDetails.getAuthorities());

    authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    if (usernamePasswordAuthenticationToken.isAuthenticated()) {
      userRepository.updateLastLoggedIn(username);
      return userDetails;
    }
    return null;
  }
}
