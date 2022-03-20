package com.familyorg.familyorganizationapp.service.impl;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.familyorg.familyorganizationapp.DTO.UserDto;
import com.familyorg.familyorganizationapp.Exception.AuthorizationException;
import com.familyorg.familyorganizationapp.Exception.BadRequestException;
import com.familyorg.familyorganizationapp.Exception.ExistingUserException;
import com.familyorg.familyorganizationapp.Exception.UserNotFoundException;
import com.familyorg.familyorganizationapp.domain.User;
import com.familyorg.familyorganizationapp.repository.UserRepository;
import com.familyorg.familyorganizationapp.service.AuthService;
import com.familyorg.familyorganizationapp.service.SecurityService;
import com.familyorg.familyorganizationapp.service.UserService;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  UserRepository userRepository;
  @Autowired
  AuthService authService;
  @Autowired
  SecurityService securityService;

  @Autowired
  BCryptPasswordEncoder bCryptPasswordEncoder;

  @Override
  public UserDto getUserData() throws AuthorizationException, UserNotFoundException {
    User requestingUser = getRequestingUser();
    return UserDto.fromUserObj(requestingUser);
  }

  @Override
  @Transactional
  public UserDto createUser(User user) throws BadRequestException, ExistingUserException {
    if (!user.isValid()) {
      throw new BadRequestException("User is missing one or more required fields");
    }
    if (!authService.verifyPasswordRequirements(user.getPassword())) {
      throw new BadRequestException("Password does not meet minimum requirements");
    }
    User existingUser = userRepository.findByUsername(user.getUsername());
    if (existingUser != null) {
      throw new ExistingUserException("Username already in use.");
    }
    existingUser = userRepository.findByEmail(user.getEmail());
    if (existingUser != null) {
      throw new ExistingUserException("Email already in use.");
    }
    user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
    User savedUser = userRepository.save(user);
    return UserDto.fromUserObj(savedUser);
  }

  @Override
  @Transactional
  public void deleteUser(String username) throws AuthorizationException, UserNotFoundException {
    User requestingUser = getRequestingUser();
    if (!username.equals(requestingUser.getUsername())) {
      throw new AuthorizationException(
          "User account requested for deletion does not match authenticated user.", true);
    }
    User user = getUserByUsername(username);
    if (user == null) {
      throw new UserNotFoundException("User not found");
    }
    userRepository.delete(user);
  }

  @Override
  @Transactional
  public void changePassword(UserDto request)
      throws AuthorizationException, UserNotFoundException, BadRequestException {
    User requestingUser = getRequestingUser();
    if (request.getOldPassword() == null || request.getNewPassword() == null
        || request.getUsername() == null) {
      throw new BadRequestException("Username, old password, and new password cannot be null");
    }
    if (!authService.verifyPasswordRequirements(request.getNewPassword())) {
      throw new BadRequestException("Password does not meet minimum requirements");
    }
    UserDetails reauthenticatedUser =
        securityService.reauthenticate(request.getUsername(), request.getOldPassword());
    if (!requestingUser.getUsername().equals(reauthenticatedUser.getUsername())) {
      throw new AuthorizationException("User is not authorized to complete this action");
    }
    requestingUser.setPassword(bCryptPasswordEncoder.encode(request.getNewPassword()));
    userRepository.save(requestingUser);
  }

  /**
   * Methods below this point should only be used by other services.
   */

  @Override
  public User getUserByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  @Override
  public User getUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  @Override
  public User getUserById(Long id) {
    Optional<User> user = userRepository.findById(id);
    if (user.isEmpty()) {
      return null;
    }
    return user.get();
  }

  @Override
  public User getRequestingUser() throws AuthorizationException, UserNotFoundException {
    UserDetails userDetails = authService.getSessionUserDetails();
    if (userDetails.getUsername() == null) {
      throw new AuthorizationException("No authenticated user found", true);
    }
    User user = getUserByUsername(userDetails.getUsername());
    if (user == null) {
      throw new UserNotFoundException("User data not found for authenticated user");
    }
    return user;
  }

  void setUserRepository(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  void setAuthService(AuthService authService) {
    this.authService = authService;
  }

}
