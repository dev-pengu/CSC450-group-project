package com.familyorg.familyorganizationapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.familyorg.familyorganizationapp.DTO.ColorDto;
import com.familyorg.familyorganizationapp.DTO.UserDto;
import com.familyorg.familyorganizationapp.DTO.builder.ColorDtoBuilder;
import com.familyorg.familyorganizationapp.DTO.builder.UserDtoBuilder;
import com.familyorg.familyorganizationapp.Exception.ApiExceptionCode;
import com.familyorg.familyorganizationapp.Exception.AuthorizationException;
import com.familyorg.familyorganizationapp.Exception.BadRequestException;
import com.familyorg.familyorganizationapp.Exception.ExistingUserException;
import com.familyorg.familyorganizationapp.Exception.UserNotFoundException;
import com.familyorg.familyorganizationapp.domain.FamilyMembers;
import com.familyorg.familyorganizationapp.domain.User;
import com.familyorg.familyorganizationapp.domain.id.FamilyMemberId;
import com.familyorg.familyorganizationapp.repository.FamilyMemberRepository;
import com.familyorg.familyorganizationapp.repository.UserRepository;
import com.familyorg.familyorganizationapp.service.AuthService;
import com.familyorg.familyorganizationapp.service.SecurityService;
import com.familyorg.familyorganizationapp.service.UserService;
import com.familyorg.familyorganizationapp.util.ColorUtil;

@Service
public class UserServiceImpl implements UserService {

  UserRepository userRepository;
  AuthService authService;
  SecurityService securityService;
  FamilyMemberRepository memberRepository;

  BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired
  public UserServiceImpl(UserRepository userRepository, AuthService authService,
      SecurityService securityService, BCryptPasswordEncoder bCryptPasswordEncoder,
      FamilyMemberRepository memberRepository) {
    super();
    this.userRepository = userRepository;
    this.authService = authService;
    this.securityService = securityService;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.memberRepository = memberRepository;

  }

  @Override
  public UserDto getUserData() throws AuthorizationException, UserNotFoundException {
    User requestingUser = getRequestingUser();
    return UserDto.fromUserObj(requestingUser);
  }

  @Override
  @Transactional
  public UserDto createUser(User user) throws BadRequestException, ExistingUserException {
    if (!user.isValid()) {
      throw new BadRequestException(ApiExceptionCode.REQUIRED_PARAM_MISSING,
          "User is missing one or more required fields");
    }
    if (!authService.verifyPasswordRequirements(user.getPassword())) {
      throw new BadRequestException(ApiExceptionCode.PASSWORD_MINIMUM_REQUIREMENTS_NOT_MET,
          "Password does not meet minimum requirements");
    }
    User existingUser = userRepository.findByUsername(user.getUsername());
    if (existingUser != null) {
      throw new ExistingUserException(ApiExceptionCode.USERNAME_IN_USE, "Username already in use.");
    }
    existingUser = userRepository.findByEmail(user.getEmail());
    if (existingUser != null) {
      throw new ExistingUserException(ApiExceptionCode.EMAIL_IN_USE, "Email already in use.");
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
      throw new AuthorizationException(ApiExceptionCode.ILLEGAL_ACTION_REQUESTED,
          "User account requested for deletion does not match authenticated user.", true);
    }
    User user = getUserByUsername(username);
    if (user == null) {
      throw new UserNotFoundException(ApiExceptionCode.USER_DOESNT_EXIST, "User not found");
    }
    if (authService.hasAuthenticatedForSensitiveActions(username)) {
      userRepository.delete(user);
    } else {
      throw new AuthorizationException(ApiExceptionCode.REAUTHENTICATION_NEEDED_FOR_REQUEST,
          "Users must reauthenticate to perform this action.");
    }
  }

  @Override
  @Transactional
  public void changePassword(UserDto request)
      throws AuthorizationException, UserNotFoundException, BadRequestException {
    User requestingUser = getRequestingUser();
    if (request.getOldPassword() == null || request.getNewPassword() == null
        || request.getUsername() == null) {
      throw new BadRequestException(ApiExceptionCode.REQUIRED_PARAM_MISSING,
          "Username, old password, and new password cannot be null");
    }
    if (!authService.verifyPasswordRequirements(request.getNewPassword())) {
      throw new BadRequestException(ApiExceptionCode.PASSWORD_MINIMUM_REQUIREMENTS_NOT_MET,
          "Password does not meet minimum requirements");
    }
    UserDetails reauthenticatedUser =
        securityService.reauthenticate(request.getUsername(), request.getOldPassword());
    if (reauthenticatedUser == null
        || !requestingUser.getUsername().equals(reauthenticatedUser.getUsername())) {
      throw new AuthorizationException(ApiExceptionCode.ILLEGAL_ACTION_REQUESTED,
          "User is not authorized to complete this action");
    }
    requestingUser.setPassword(bCryptPasswordEncoder.encode(request.getNewPassword()));
    userRepository.save(requestingUser);
  }

  @Override
  public UserDto getSettingsForUser() {
    User requestingUser = getRequestingUser();

    List<ColorDto> colorsByFamily = memberRepository.getColorsByUser(requestingUser.getId());
    return new UserDtoBuilder()
        .fromUserObj(requestingUser)
        .withColorsByFamily(colorsByFamily)
        .build();
  }

  @Override
  public UserDto updateUserSettingsAndData(UserDto request) {
    User requestingUser = getRequestingUser();
    if (!requestingUser.getId().equals(request.getId())) {
      throw new AuthorizationException(ApiExceptionCode.USER_NOT_LOGGED_IN,
          "User id on request does not match currently logged in user.");
    }

    if (!requestingUser.getFirstName().equals(request.getFirstName())) {
      requestingUser.setFirstName(request.getFirstName());
    }
    if (!requestingUser.getLastName().equals(request.getLastName())) {
      requestingUser.setLastName(request.getLastName());
    }
    if (requestingUser.useDarkMode() != request.getUseDarkMode()) {
      requestingUser.setDarkMode(request.getUseDarkMode());
    }

    List<ColorDto> colors = new ArrayList<>();
    if (request.getColorsByFamily() != null) {

      List<FamilyMembers> updated = request.getColorsByFamily().stream().map(color -> {
        if (!ColorUtil.isValidHexCode(color.getColor())) {
          throw new BadRequestException(ApiExceptionCode.BAD_PARAM_VALUE,
              "Color for family " + color.getFamily() + " is not a valid hexcode");
        }
        Optional<FamilyMembers> familyMembers = memberRepository
            .findById(new FamilyMemberId(requestingUser.getId(), color.getFamilyId()));

        if (familyMembers.isPresent()) {
          familyMembers.get().setEventColor(color.getColor());
          return familyMembers.get();
        }
        return null;
      }).filter(member -> member != null).collect(Collectors.toList());

      Iterable<FamilyMembers> savedMembers = memberRepository.saveAll(updated);

      savedMembers.forEach(member -> {
        colors.add(new ColorDtoBuilder().withFamily(member.getFamily().getName())
            .withFamilyId(member.getFamily().getId())
            .withUserId(requestingUser.getId())
            .withColor(member.getEventColor())
            .build());
      });
    }
    User savedUser = userRepository.save(requestingUser);
    return new UserDtoBuilder().fromUserObj(savedUser).withColorsByFamily(colors).build();
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
      throw new AuthorizationException(ApiExceptionCode.USER_NOT_LOGGED_IN,
          "No authenticated user found", true);
    }
    User user = getUserByUsername(userDetails.getUsername());
    if (user == null) {
      throw new UserNotFoundException(ApiExceptionCode.USER_DOESNT_EXIST,
          "User data not found for authenticated user");
    }
    return user;
  }

  @Override
  public UserDto updateUser(UserDto request) {
    User requestingUser = getRequestingUser();
    requestingUser.setFirstName(request.getFirstName());
    requestingUser.setLastName(requestingUser.getLastName());
    requestingUser.setTimezone(request.getTimezone());
    User savedUser = userRepository.save(requestingUser);
    return UserDto.fromUserObj(savedUser);
  }
}
