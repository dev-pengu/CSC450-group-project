package com.familyorg.familyorganizationapp.service.impl;

import com.familyorg.familyorganizationapp.Exception.BadRequestException;
import com.familyorg.familyorganizationapp.domain.PasswordResetCode;
import com.familyorg.familyorganizationapp.repository.PasswordResetCodeRepository;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.familyorg.familyorganizationapp.Exception.ApiExceptionCode;
import com.familyorg.familyorganizationapp.Exception.UserNotFoundException;
import com.familyorg.familyorganizationapp.domain.User;
import com.familyorg.familyorganizationapp.repository.UserRepository;
import com.familyorg.familyorganizationapp.service.AuthService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

  @Autowired UserRepository userRepository;
  @Autowired PasswordResetCodeRepository codeRepository;

  private final Pattern passwordPattern =
      Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()-+?]).+$");

  @Override
  public UserDetails loadUserByUsername(String username) throws UserNotFoundException {
    User user = userRepository.findByUsernameIgnoreCase(username);
    if (user == null) {
      throw new UserNotFoundException(ApiExceptionCode.USER_DOESNT_EXIST, "Username not found");
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
    Objects.requireNonNull(password);
    if (password.length() < 12) {
      return false;
    }
    return passwordPattern.matcher(password).matches();
  }

  @Override
  public boolean hasAuthenticatedForSensitiveActions(String username) {
    Objects.requireNonNull(username);
    User user = userRepository.findByUsernameIgnoreCase(username);
    Calendar cal = Calendar.getInstance();
    cal.setTime(Date.from(Instant.now()));
    cal.add(Calendar.MINUTE, -15);
    if (user.getLastLoggedIn().compareTo(cal.getTime()) > 0) {
      return true;
    }
    return false;
  }

  @Override
  @Transactional
  public String generateResetCode(User user) {
    if (user == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "User cannot be null.");
    }
    List<PasswordResetCode> existingCodes = codeRepository.getResetCodesByUser(user.getId());
    if (existingCodes.size() > 0) {
      codeRepository.batchExpire(
          existingCodes.stream().map(PasswordResetCode::getId).collect(Collectors.toList()));
    }
    PasswordResetCode code = new PasswordResetCode();
    code.setUser(user);
    code.setResetCode(UUID.randomUUID().toString());
    code.setCreated(Timestamp.from(Instant.now()));
    PasswordResetCode savedCode = codeRepository.save(code);
    return savedCode.getResetCode();
  }

  @Override
  @Transactional
  public boolean verifyResetCode(String resetCode, User user) {
    if (resetCode == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Reset Code cannot be null.");
    }
    if (user == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "User cannot be null.");
    }
    PasswordResetCode code = codeRepository.findByResetCode(resetCode);
    if (code == null || code.getExpired()) {
      return false;
    }
    Calendar cal = Calendar.getInstance();
    cal.setTime(Date.from(Instant.now()));
    cal.add(Calendar.MINUTE, 15);
    if (code.getUser().getId().equals(user.getId())
        && code.getCreated().compareTo(cal.getTime()) < 0) {
      code.setExpired(true);
      codeRepository.save(code);
      return true;
    }
    code.setExpired(true);
    codeRepository.save(code);
    return false;
  }
}
