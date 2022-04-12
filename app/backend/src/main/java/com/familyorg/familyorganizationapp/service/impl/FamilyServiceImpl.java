package com.familyorg.familyorganizationapp.service.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.familyorg.familyorganizationapp.DTO.FamilyDto;
import com.familyorg.familyorganizationapp.DTO.FamilyMemberDto;
import com.familyorg.familyorganizationapp.DTO.FamilyRoleUpdateRequest;
import com.familyorg.familyorganizationapp.Exception.ApiExceptionCode;
import com.familyorg.familyorganizationapp.Exception.AuthorizationException;
import com.familyorg.familyorganizationapp.Exception.BadRequestException;
import com.familyorg.familyorganizationapp.Exception.ResourceNotFoundException;
import com.familyorg.familyorganizationapp.Exception.UserNotFoundException;
import com.familyorg.familyorganizationapp.domain.Calendar;
import com.familyorg.familyorganizationapp.domain.Family;
import com.familyorg.familyorganizationapp.domain.FamilyMembers;
import com.familyorg.familyorganizationapp.domain.Role;
import com.familyorg.familyorganizationapp.domain.User;
import com.familyorg.familyorganizationapp.domain.id.FamilyMemberId;
import com.familyorg.familyorganizationapp.repository.CalendarRepository;
import com.familyorg.familyorganizationapp.repository.FamilyMemberRepository;
import com.familyorg.familyorganizationapp.repository.FamilyRepository;
import com.familyorg.familyorganizationapp.service.FamilyService;
import com.familyorg.familyorganizationapp.service.UserService;
import com.familyorg.familyorganizationapp.util.ColorUtil;

@Service
public class FamilyServiceImpl implements FamilyService {
  Logger LOG = LoggerFactory.getLogger(FamilyServiceImpl.class);

  FamilyRepository familyRepository;
  FamilyMemberRepository familyMemberRepository;
  CalendarRepository calendarRepository;
  UserService userService;

  @Autowired
  public FamilyServiceImpl(FamilyRepository familyRepository,
      FamilyMemberRepository familyMemberRepository, CalendarRepository calendarRepository,
      UserService userService) {
    this.familyRepository = familyRepository;
    this.familyMemberRepository = familyMemberRepository;
    this.calendarRepository = calendarRepository;
    this.userService = userService;
  }

  @Override
  @Transactional
  public FamilyDto createFamily(FamilyDto familyRequest)
      throws BadRequestException, UserNotFoundException {
    if (familyRequest.getEventColor() == null || familyRequest.getName() == null
        || familyRequest.getOwner() == null || familyRequest.getTimezone() == null) {
      throw new BadRequestException(ApiExceptionCode.REQUIRED_PARAM_MISSING,
          "Request is missing one or more required fields");
    }
    if (!ColorUtil.isValidHexCode(familyRequest.getEventColor())) {
      throw new BadRequestException(ApiExceptionCode.BAD_PARAM_VALUE,
          "Family event color is not a valid hexcode.");
    }
    if (!ColorUtil.isValidHexCode(familyRequest.getOwner().getEventColor())) {
      throw new BadRequestException(ApiExceptionCode.BAD_PARAM_VALUE,
          "Owner event color is not a valid hexcode.");
    }
    User ownerUser = userService.getRequestingUser();
    Family family = new Family();
    family.setEventColor(familyRequest.getEventColor());
    family.setName(familyRequest.getName());
    family.setTimezone(familyRequest.getTimezone());
    FamilyMemberDto owner = familyRequest.getOwner();

    Family savedFamily = familyRepository.save(family);
    Calendar calendar = new Calendar();
    calendar.setDefault(true);
    calendar.setDescription("Family Calendar");
    calendar.setFamily(family);
    Calendar savedCalendar = calendarRepository.save(calendar);
    savedFamily.addCalendar(savedCalendar);
    FamilyMembers ownerRelation = new FamilyMembers();
    ownerRelation.setFamily(savedFamily);
    ownerRelation.setEventColor(owner.getEventColor());
    ownerRelation.setRole(Role.OWNER);
    ownerRelation.setUser(ownerUser);
    familyMemberRepository.save(ownerRelation);
    savedFamily.addMember(ownerRelation);
    familyRepository.save(savedFamily);
    return FamilyDto.fromFamilyObj(savedFamily, ownerUser);
  }

  @Override
  public FamilyDto getFamily(FamilyDto familyRequest) {
    Optional<Family> family = familyRepository.findById(familyRequest.getId());

    if (family.isEmpty()) {
      throw new ResourceNotFoundException(ApiExceptionCode.FAMILY_DOESNT_EXIST,
          "Family with id " + familyRequest.getId() + " not found");
    }

    User requestingUser = userService.getRequestingUser();
    boolean hasAppropriatePermissions =
        verfiyMinimumRoleSecurity(family.get(), requestingUser, Role.CHILD);
    if (!hasAppropriatePermissions) {
      throw new AuthorizationException(ApiExceptionCode.USER_PRIVILEGES_TOO_LOW,
          "User not authorized to complete this action.");
    }

    Family familyObj = family.get();
    return FamilyDto.fromFamilyObj(familyObj, requestingUser);
  }

  @Override
  public List<FamilyDto> getFamiliesByUser() throws UserNotFoundException {
    User requestingUser = userService.getRequestingUser();

    List<Family> families = familyRepository.getFamiliesByUserId(requestingUser.getId());
    if (families == null) {
      return Collections.emptyList();
    }

    return families.stream()
        .map(family -> FamilyDto.fromFamilyObj(family, requestingUser))
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public FamilyDto updateFamily(FamilyDto familyRequest)
      throws AuthorizationException, UserNotFoundException {
    if (familyRequest.getId() == null) {
      throw new BadRequestException(ApiExceptionCode.REQUIRED_PARAM_MISSING,
          "Id field is required to update a family.");
    }
    if (familyRequest.getEventColor() != null
        && !ColorUtil.isValidHexCode(familyRequest.getEventColor())) {
      throw new BadRequestException(ApiExceptionCode.BAD_PARAM_VALUE,
          "Family event color is not a valid hexcode.");
    }
    User requestingUser = userService.getRequestingUser();

    Optional<Family> family = familyRepository.findById(familyRequest.getId());
    if (family.isEmpty()) {
      throw new ResourceNotFoundException(ApiExceptionCode.FAMILY_DOESNT_EXIST,
          "Family with id " + familyRequest.getId() + " not found.");
    }
    boolean hasAppropriatePermissions =
        verfiyMinimumRoleSecurity(family.get(), requestingUser, Role.ADMIN);
    if (!hasAppropriatePermissions) {
      throw new AuthorizationException(ApiExceptionCode.USER_PRIVILEGES_TOO_LOW,
          "User not authorized to complete this action.");
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
      family.get().setEventColor(familyRequest.getEventColor());
    }

    Family saved = familyRepository.save(family.get());
    return FamilyDto.fromFamilyObj(saved, requestingUser);
  }



  @Override
  @Transactional
  public void deleteFamily(Long id) throws AuthorizationException {
    if (id == null) {
      throw new BadRequestException(ApiExceptionCode.REQUIRED_PARAM_MISSING,
          "Id is required to delete a family.");
    }
    User requestingUser = userService.getRequestingUser();
    Optional<Family> family = familyRepository.findById(id);
    boolean hasAppropriatePermissions =
        verfiyMinimumRoleSecurity(family.get(), requestingUser, Role.OWNER);
    if (!hasAppropriatePermissions) {
      throw new AuthorizationException(ApiExceptionCode.USER_PRIVILEGES_TOO_LOW,
          "User not authorized to complete this action.");
    }
    familyRepository.deleteById(id);
  }

  @Transactional
  @Override
  public FamilyDto transferOwnership(FamilyDto request) {
    User requestingUser = userService.getRequestingUser();
    Optional<Family> family = familyRepository.findById(request.getId());
    if (family.isEmpty()) {
      throw new ResourceNotFoundException(ApiExceptionCode.FAMILY_DOESNT_EXIST,
          "Family with id " + request.getId() + " not found.");
    }
    boolean hasAppropriatePermissions =
        verfiyMinimumRoleSecurity(family.get(), requestingUser, Role.OWNER);
    if (!hasAppropriatePermissions) {
      throw new AuthorizationException(ApiExceptionCode.USER_PRIVILEGES_TOO_LOW,
          "User not authorized to complete this action.");
    }
    Optional<FamilyMembers> currentOwnerRelation = family.get()
        .getMembers()
        .stream()
        .filter(member -> member.getRole() == Role.OWNER)
        .findFirst();

    // if owner on request is the same as the current owner, no work needs to be done, just return
    // the current family object
    if (request.getOwner().getUser().getUsername().equals(requestingUser.getUsername())) {
      return FamilyDto.fromFamilyObj(family.get(), requestingUser);
    }

    User newOwner = userService.getUserByUsername(request.getOwner().getUser().getUsername());
    if (newOwner == null) {
      throw new UserNotFoundException(ApiExceptionCode.USER_DOESNT_EXIST,
          "User supplied to be new owner does not exist.", false);
    }

    Optional<FamilyMembers> newOwnerRelation = family.get()
        .getMembers()
        .stream()
        .filter(member -> member.getUser().getId() == request.getOwner().getUser().getId())
        .findFirst();
    if (newOwnerRelation.isEmpty()) {
      throw new UserNotFoundException(ApiExceptionCode.USER_NOT_IN_FAMILY,
          "User supplied to be new owner is not a member of family with id " + family.get().getId(),
          false);
    }

    currentOwnerRelation.get().setRole(Role.ADMIN);
    newOwnerRelation.get().setRole(Role.OWNER);
    familyMemberRepository.save(currentOwnerRelation.get());
    familyMemberRepository.save(newOwnerRelation.get());

    Optional<Family> updatedFamily = familyRepository.findById(request.getId());
    return FamilyDto.fromFamilyObj(updatedFamily.get(), requestingUser);
  }

  @Override
  public void updateMemberRoles(FamilyRoleUpdateRequest request) {
    User requestingUser = userService.getRequestingUser();
    if (request.getFamilyId() == null) {
      throw new BadRequestException(ApiExceptionCode.REQUIRED_PARAM_MISSING,
          "Family id may not be null.");
    }
    if (request.getMembers() == null || request.getMembers().isEmpty()) {
      throw new BadRequestException(ApiExceptionCode.REQUIRED_PARAM_MISSING,
          "Family members to update must be specified.");
    }
    Optional<Family> family = familyRepository.findById(request.getFamilyId());
    if (family.isEmpty()) {
      throw new ResourceNotFoundException(ApiExceptionCode.FAMILY_DOESNT_EXIST,
          "Family with id " + request.getFamilyId() + " not found.");
    }
    boolean hasAppropriatePermissions =
        verfiyMinimumRoleSecurity(family.get(), requestingUser, Role.ADMIN);
    if (!hasAppropriatePermissions) {
      throw new AuthorizationException(ApiExceptionCode.USER_PRIVILEGES_TOO_LOW,
          "User not authorized to complete this action.");
    }
    Set<FamilyMembers> membersToUpdate = new HashSet<>();
    request.getMembers().forEach(member -> {
      FamilyMemberId id = new FamilyMemberId(member.getUser().getId(), family.get().getId());
      Optional<FamilyMembers> familyMemberOpt = familyMemberRepository.findById(id);
      if (familyMemberOpt.isEmpty()) {
        throw new UserNotFoundException(ApiExceptionCode.USER_DOESNT_EXIST,
            "User with id " + member.getUser().getId() + " not found.");
      }
      FamilyMembers familyMember = familyMemberOpt.get();
      if (member.getRole() != Role.OWNER || familyMember.getRole() != Role.OWNER) {
        familyMember.setRole(member.getRole());
        membersToUpdate.add(familyMember);
      }
    });
    if (!membersToUpdate.isEmpty()) {
      familyMemberRepository.saveAll(membersToUpdate);
    }
  }

  /**
   * Methods below should only be called from other services and null checking should be used as it
   * is not guaranteed a result will be returned.
   */

  @Override
  public Family getFamilyByInviteCode(String inviteCode) {
    return familyRepository.findByInviteCode(inviteCode);
  }

  @Override
  public boolean verfiyMinimumRoleSecurity(Family family, User user, Role minimumRole) {
    return family.getMembers()
        .stream()
        .filter(member -> member.getUser().getUsername().equals(user.getUsername())
            && member.getRole().getLevel() >= minimumRole.getLevel())
        .count() > 0;
  }

  @Override
  public List<Long> getFamilyIdsByUser(String username) {
    return familyRepository.getFamilyIdsByUser(username);
  }

  @Override
  public List<Family> getFamiliesByUser(String username) {
    return familyRepository.getFamiliesByUser(username);
  }

  @Override
  @Transactional
  public Family updateFamily(Family family) {
    Family updatedFamily = familyRepository.save(family);
    return updatedFamily;
  }

  @Override
  public Optional<Family> getFamilyById(Long id) {
    return familyRepository.findById(id);
  }

}
