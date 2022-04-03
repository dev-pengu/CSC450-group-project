package com.familyorg.familyorganizationapp.service;

import com.familyorg.familyorganizationapp.DTO.UserDto;
import com.familyorg.familyorganizationapp.Exception.AuthorizationException;
import com.familyorg.familyorganizationapp.Exception.BadRequestException;
import com.familyorg.familyorganizationapp.Exception.ExistingUserException;
import com.familyorg.familyorganizationapp.Exception.UserNotFoundException;
import com.familyorg.familyorganizationapp.domain.User;

public interface UserService {
  UserDto createUser(User user) throws BadRequestException, ExistingUserException;

  UserDto getUserData() throws AuthorizationException, UserNotFoundException;

  User getUserByUsername(String username);

  User getUserByEmail(String email);

  User getUserById(Long id);

  void deleteUser(String username) throws AuthorizationException, UserNotFoundException;

  User getRequestingUser() throws AuthorizationException, UserNotFoundException;

  void changePassword(UserDto request)
      throws AuthorizationException, UserNotFoundException, BadRequestException;

  UserDto updateUser(UserDto request);

  UserDto getSettingsForUser();

  UserDto updateUserSettingsAndData(UserDto request);

  void updateUser(User user);
}
