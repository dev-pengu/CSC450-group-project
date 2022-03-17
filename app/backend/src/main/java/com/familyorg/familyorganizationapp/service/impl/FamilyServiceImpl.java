package com.familyorg.familyorganizationapp.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.familyorg.familyorganizationapp.DTO.FamilyDto;
import com.familyorg.familyorganizationapp.DTO.FamilyMemberDto;
import com.familyorg.familyorganizationapp.Exception.AuthorizationException;
import com.familyorg.familyorganizationapp.Exception.BadRequestException;
import com.familyorg.familyorganizationapp.Exception.FamilyNotFoundException;
import com.familyorg.familyorganizationapp.Exception.UserNotFoundException;
import com.familyorg.familyorganizationapp.domain.Family;
import com.familyorg.familyorganizationapp.domain.FamilyMembers;
import com.familyorg.familyorganizationapp.domain.Role;
import com.familyorg.familyorganizationapp.domain.User;
import com.familyorg.familyorganizationapp.domain.id.FamilyMemberId;
import com.familyorg.familyorganizationapp.repository.FamilyMemberRepository;
import com.familyorg.familyorganizationapp.repository.FamilyRepository;
import com.familyorg.familyorganizationapp.service.AuthService;
import com.familyorg.familyorganizationapp.service.FamilyService;
import com.familyorg.familyorganizationapp.service.UserService;
import com.familyorg.familyorganizationapp.util.ColorUtil;

@Service
public class FamilyServiceImpl implements FamilyService {
  Logger LOG = LoggerFactory.getLogger(FamilyServiceImpl.class);

  @Autowired
  FamilyRepository familyRepository;
  @Autowired
  FamilyMemberRepository familyMemberRepository;
  @Autowired
  UserService userService;
  @Autowired
  AuthService authService;

  @Override
  @Transactional
  public FamilyDto createFamily(FamilyDto familyRequest)
      throws BadRequestException, UserNotFoundException {
    UserDetails userDetails = authService.getSessionUserDetails();
    if (userDetails.getUsername() == null) {
      throw new AuthorizationException("No authenticated user found", true);
    }
    User ownerUser = userService.getUserByUsername(userDetails.getUsername());
    if (ownerUser == null) {
      throw new UserNotFoundException("User not found");
    }
    if (familyRequest.getEventColor() == null || familyRequest.getName() == null
        || familyRequest.getOwner() == null || familyRequest.getTimezone() == null) {
      throw new BadRequestException("Request is missing one or more required fields");
    }
    if (!ColorUtil.isValidHexCode(familyRequest.getEventColor())) {
      throw new BadRequestException("Family event color is not a valid hexcode.");
    }
    if (!ColorUtil.isValidHexCode(familyRequest.getOwner().getEventColor())) {
      throw new BadRequestException("Owner event color is not a valid hexcode.");
    }
    Family family = new Family();
    family.setEventColor(familyRequest.getEventColor());
    family.setName(familyRequest.getName());
    family.setTimezone(familyRequest.getTimezone());
    FamilyMemberDto owner = familyRequest.getOwner();
    Family savedFamily;
    try {
      savedFamily = familyRepository.save(family);
    } catch (DataIntegrityViolationException e) {
      throw new BadRequestException("Request is missing one or more required fields.");
    }
    FamilyMembers ownerRelation = new FamilyMembers();
    ownerRelation.setFamily(savedFamily);
    ownerRelation.setEventColor(owner.getEventColor());
    ownerRelation.setRole(Role.OWNER);
    ownerRelation.setUser(ownerUser);
    familyMemberRepository.save(ownerRelation);
    savedFamily.addMember(ownerRelation);
    return FamilyDto.fromFamilyObj(savedFamily, ownerUser);
  }

  @Override
  public FamilyDto getFamily(FamilyDto familyRequest) throws FamilyNotFoundException {
    Optional<Family> family = familyRepository.findById(familyRequest.getId());

    if (family.isEmpty()) {
      throw new FamilyNotFoundException("Family not found");
    }
    UserDetails userDetails = authService.getSessionUserDetails();
    if (userDetails.getUsername() == null) {
      throw new AuthorizationException("No authenticated user found", true);
    }
    User requestingUser = userService.getUserByUsername(userDetails.getUsername());

    if (requestingUser == null) {
      throw new UserNotFoundException("User not found");
    }
    final Long requestingUserId = requestingUser.getId();
    family.get().getMembers().stream()
        .filter(familyMember -> familyMember.getUser().getId().equals(requestingUserId)).findAny()
        .orElseThrow(AuthorizationException::new);
    Family familyObj = family.get();
    return FamilyDto.fromFamilyObj(familyObj, requestingUser);
  }

  @Override
  public Optional<Family> getFamilyById(Long id) {
    return familyRepository.findById(id);
  }

  @Override
  public List<FamilyDto> getFamiliesByUser(Long userId) throws UserNotFoundException {
    UserDetails userDetails = authService.getSessionUserDetails();
    if (userDetails.getUsername() == null) {
      throw new AuthorizationException("No authenticated user found", true);
    }
    User user = userService.getUserById(userId);
    if (user == null) {
      throw new UserNotFoundException("User with id " + userId + " not found");
    }
    if (!user.getUsername().equals(userDetails.getUsername())) {
      throw new AuthorizationException("User supplied does not match current authenticated user",
          false);
    }
    List<Family> families = familyRepository.getFamiliesByUserId(userId);
    if (families == null) {
      return Collections.emptyList();
    }

    return families.stream().map(family -> FamilyDto.fromFamilyObj(family, user))
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public FamilyDto updateFamily(FamilyDto familyRequest)
      throws FamilyNotFoundException, AuthorizationException, UserNotFoundException {
    UserDetails userDetails = authService.getSessionUserDetails();
    if (userDetails.getUsername() == null) {
      throw new AuthorizationException("No authenticated user found", true);
    }
    User user = userService.getUserByUsername(userDetails.getUsername());
    if (user == null) {
      throw new UserNotFoundException("User not found");
    }

    Optional<Family> family = familyRepository.findById(familyRequest.getId());
    if (family.isEmpty()) {
      throw new FamilyNotFoundException("Unable to find family with given id");
    }
    if (familyRequest.getInviteCode() != null) {
      family.get().setInviteCode(familyRequest.getInviteCode());
    }
    if (familyRequest.getName() != null) {
      family.get().setName(familyRequest.getName());
    }
    if (familyRequest.getTimezone() != null) {
      family.get().setTimezone(familyRequest.getTimezone());
    }
    if (familyRequest.getEventColor() != null) {
      if (!ColorUtil.isValidHexCode(familyRequest.getEventColor())) {
        throw new BadRequestException("Family event color is not a valid hexcode.");
      }
      family.get().setEventColor(familyRequest.getEventColor());
    }

    Optional<FamilyMembers> familyRelation =
        familyMemberRepository.findById(new FamilyMemberId(user.getId(), family.get().getId()));
    if (familyRelation.isEmpty()) {
      throw new AuthorizationException("Unable to find relation to family.", false);
    } else {
      if (familyRelation.get().getRole().getLevel() >= Role.ADMIN.getLevel()) {
        Family saved = familyRepository.save(family.get());
        return FamilyDto.fromFamilyObj(saved, user);
      } else {
        throw new AuthorizationException("You are not authorized to complete this action", false);
      }
    }
  }

  @Override
  @Transactional
  public Family updateFamily(Family family) {
    Family updatedFamily = familyRepository.save(family);
    return updatedFamily;
  }

  @Override
  @Transactional
  public void deleteFamily(Long id) throws FamilyNotFoundException, AuthorizationException {
    UserDetails userDetails = authService.getSessionUserDetails();
    if (userDetails.getUsername() == null) {
      throw new AuthorizationException("No authenticated user found", true);
    }
    User user = userService.getUserByUsername(userDetails.getUsername());
    if (user == null) {
      throw new UserNotFoundException("User not found");
    }
    Optional<FamilyMembers> familyRelation =
        familyMemberRepository.findById(new FamilyMemberId(user.getId(), id));
    if (familyRelation.isEmpty()) {
      throw new AuthorizationException("Unable to find relation to family.", false);
    } else {
      if (familyRelation.get().getRole().equals(Role.OWNER)) {
        familyRepository.deleteById(id);
      } else {
        throw new AuthorizationException("You are not authorized to complete this action", false);
      }
    }
  }

  @Transactional
  @Override
  public FamilyDto transferOwnership(FamilyDto request) {
    UserDetails userDetails = authService.getSessionUserDetails();
    if (userDetails.getUsername() == null) {
      throw new AuthorizationException("No authenticated user found", true);
    }
    User user = userService.getUserByUsername(userDetails.getUsername());
    if (user == null) {
      throw new UserNotFoundException("User not found", true);
    }
    Optional<Family> family = familyRepository.findById(request.getId());
    if (family.isEmpty()) {
      throw new FamilyNotFoundException("Family with id " + request.getId() + " not found.");
    }
    Optional<FamilyMembers> familyRelation =
        familyMemberRepository.findById(new FamilyMemberId(user.getId(), family.get().getId()));
    if (familyRelation.isEmpty() || familyRelation.get().getRole() != Role.OWNER) {
      throw new AuthorizationException(
          "User with username " + user.getUsername() + " not authorized to perform this action.",
          false);
    }
    // if owner on request is the same as the current owner, no work needs to be done, just return
    // the current family object
    if (request.getOwner().getUser().getUsername().equals(user.getUsername())) {
      return FamilyDto.fromFamilyObj(family.get(), user);
    }

    User newOwner = userService.getUserByUsername(request.getOwner().getUser().getUsername());
    if (newOwner == null) {
      throw new UserNotFoundException("User supplied to be new owner does not exist.", false);
    }
    Optional<FamilyMembers> newOwnerRelation =
        familyMemberRepository.findById(new FamilyMemberId(newOwner.getId(), family.get().getId()));
    if (newOwnerRelation.isEmpty()) {
      throw new UserNotFoundException(
          "User supplied to be new owner is not a member of family with id " + family.get().getId(),
          false);
    }

    familyRelation.get().setRole(Role.ADMIN);
    newOwnerRelation.get().setRole(Role.OWNER);
    familyMemberRepository.save(familyRelation.get());
    familyMemberRepository.save(newOwnerRelation.get());

    Optional<Family> updatedFamily = familyRepository.findById(request.getId());
    return FamilyDto.fromFamilyObj(updatedFamily.get(), user);
  }

  @Override
  public Family getFamilyByInviteCode(String inviteCode) {
    return familyRepository.findByInviteCode(inviteCode);
  }

  /**
   * This should only be called for testing to mock the injected class
   *
   * @param familyRepository
   */
  void setFamilyRepository(FamilyRepository familyRepository) {
    this.familyRepository = familyRepository;
  }

  /**
   * This should only be called for testing to mock the injected class
   *
   * @param familyMemberRepository
   */
  void setFamilyMemberRepository(FamilyMemberRepository familyMemberRepository) {
    this.familyMemberRepository = familyMemberRepository;
  }

  /**
   * This should only be called for testing to mock the injected class
   *
   * @param userService
   */
  void setUserService(UserService userService) {
    this.userService = userService;
  }

  /**
   * This should only be called for testing to mock the injected class
   *
   * @param authService
   */
  void setAuthService(AuthService authService) {
    this.authService = authService;
  }

}
