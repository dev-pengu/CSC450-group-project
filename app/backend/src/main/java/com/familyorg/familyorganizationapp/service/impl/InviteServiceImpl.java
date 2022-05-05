package com.familyorg.familyorganizationapp.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.familyorg.familyorganizationapp.DTO.FamilyDto;
import com.familyorg.familyorganizationapp.Exception.ApiExceptionCode;
import com.familyorg.familyorganizationapp.Exception.AuthorizationException;
import com.familyorg.familyorganizationapp.Exception.BadRequestException;
import com.familyorg.familyorganizationapp.Exception.ResourceNotFoundException;
import com.familyorg.familyorganizationapp.domain.Family;
import com.familyorg.familyorganizationapp.domain.FamilyMembers;
import com.familyorg.familyorganizationapp.domain.InviteCode;
import com.familyorg.familyorganizationapp.domain.MemberInvite;
import com.familyorg.familyorganizationapp.domain.Role;
import com.familyorg.familyorganizationapp.domain.User;
import com.familyorg.familyorganizationapp.repository.MemberInviteRepository;
import com.familyorg.familyorganizationapp.service.AuthService;
import com.familyorg.familyorganizationapp.service.FamilyService;
import com.familyorg.familyorganizationapp.service.InviteService;
import com.familyorg.familyorganizationapp.service.UserService;
import com.familyorg.familyorganizationapp.util.ColorUtil;

@Service
public class InviteServiceImpl implements InviteService {
  private Logger logger = LoggerFactory.getLogger(InviteServiceImpl.class);

  MemberInviteRepository memberInviteRepository;
  FamilyService familyService;
  AuthService authService;
  UserService userService;

  @Autowired
  public InviteServiceImpl(
      MemberInviteRepository memberInviteRepository,
      FamilyService familyService,
      AuthService authService,
      UserService userService) {
    this.memberInviteRepository = memberInviteRepository;
    this.familyService = familyService;
    this.authService = authService;
    this.userService = userService;
  }

  @Override
  @Transactional
  public MemberInvite createUniqueMemberInvite(Long familyId, String userEmail)
      throws AuthorizationException {
    if (familyId == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Family id cannot be null.");
    }
    if (userEmail == null || userEmail.isBlank()) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Recipient cannot be null.");
    }
    Optional<Family> family = familyService.getFamilyById(familyId);
    if (family.isEmpty()) {
      throw new ResourceNotFoundException(
          ApiExceptionCode.FAMILY_DOESNT_EXIST, "Family with id " + familyId + " not found");
    }
    User requestingUser = userService.getRequestingUser();
    boolean hasAppropriatePermissions =
        familyService.verfiyMinimumRoleSecurity(family.get(), requestingUser, Role.ADULT);
    if (!hasAppropriatePermissions)
      throw new AuthorizationException(
          ApiExceptionCode.USER_PRIVILEGES_TOO_LOW, "User not authorized to complete this action.");
    MemberInvite invite = new MemberInvite(family.get(), userEmail);
    MemberInvite savedInvite = memberInviteRepository.save(invite);
    return savedInvite;
  }

  @Override
  @Transactional
  public MemberInvite createUniqueMemberInviteWithRole(Long familyId, String userEmail, Role role)
      throws AuthorizationException {
    if (familyId == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Family id cannot be null.");
    }
    if (userEmail == null || userEmail.isBlank()) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Recipient cannot be null.");
    }
    if (role == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Role cannot be null.");
    }
    Optional<Family> family = familyService.getFamilyById(familyId);
    if (family.isEmpty()) {
      throw new ResourceNotFoundException(
          ApiExceptionCode.FAMILY_DOESNT_EXIST, "Family with id " + familyId + " not found");
    }
    User requestingUser = userService.getRequestingUser();
    boolean hasAppropriatePermissions =
        familyService.verfiyMinimumRoleSecurity(family.get(), requestingUser, Role.ADULT);
    if (!hasAppropriatePermissions)
      throw new AuthorizationException(
          ApiExceptionCode.USER_PRIVILEGES_TOO_LOW, "User not authorized to complete this action.");
    MemberInvite invite = new MemberInvite(family.get(), userEmail, role);
    MemberInvite savedInvite = memberInviteRepository.save(invite);
    return savedInvite;
  }

  @Override
  @Transactional
  public FamilyDto generatePersistentMemberInvite(Long familyId) throws AuthorizationException {
    if (familyId == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Family id cannot be null.");
    }
    Optional<Family> family = familyService.getFamilyById(familyId);
    if (family.isEmpty()) {
      throw new ResourceNotFoundException(
          ApiExceptionCode.FAMILY_DOESNT_EXIST, "Family with id " + familyId + " not found");
    }
    User requestingUser = userService.getRequestingUser();
    boolean hasAppropriatePermissions =
        familyService.verfiyMinimumRoleSecurity(family.get(), requestingUser, Role.ADMIN);
    if (!hasAppropriatePermissions)
      throw new AuthorizationException(
          ApiExceptionCode.USER_PRIVILEGES_TOO_LOW, "User not authorized to complete this action.");

    InviteCode inviteCode = new InviteCode(true);
    family.get().setInviteCode(inviteCode);
    Family updatedFamily = familyService.updateFamily(family.get());
    return FamilyDto.fromFamilyObj(
        updatedFamily, userService.getUserByUsername(requestingUser.getUsername()));
  }

  @Override
  @Transactional
  public void verifyMemberInvite(InviteCode invite, String eventColor)
      throws AuthorizationException {
    if (invite == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Invite code cannot be null.");
    }
    if (eventColor == null || !ColorUtil.isValidHexCode(eventColor)) {
      throw new BadRequestException(
          ApiExceptionCode.BAD_PARAM_VALUE, "Event color is not a valid hexcode");
    }
    User requestingUser = userService.getRequestingUser();

    if (invite.isPersistent()) {
      // Find the family with the persistent invite code
      Family family = familyService.getFamilyByInviteCode(invite.toString());
      if (family == null) {
        throw new ResourceNotFoundException(
            ApiExceptionCode.INVITE_CODE_DOESNT_EXIST, "Invalid invite code");
      }
      // Add the user to the family
      FamilyMembers member = new FamilyMembers(requestingUser, family, Role.CHILD, eventColor);
      family.addMember(member);
      familyService.updateFamily(family);
    } else {
      // Get the invite data
      MemberInvite memberInvite = memberInviteRepository.findByInviteCode(invite.toString());
      if (memberInvite == null) {
        throw new ResourceNotFoundException(
            ApiExceptionCode.INVITE_CODE_DOESNT_EXIST, "Invalid invite code");
      }
      if (!memberInvite.getUserEmail().equals(requestingUser.getEmail())) {
        throw new AuthorizationException(
            ApiExceptionCode.ILLEGAL_ACTION_REQUESTED,
            "You are not authorized to use this invite code",
            false);
      }
      // Get the family from the invite
      Optional<Family> family = familyService.getFamilyById(memberInvite.getFamilyId());
      if (family.isEmpty()) {
        throw new ResourceNotFoundException(
            ApiExceptionCode.FAMILY_DOESNT_EXIST,
            "Family not found for invite code "
                + invite.toString()
                + ". The family may have been removed before you joined.");
      }
      // Add the user to the family
      FamilyMembers member =
          new FamilyMembers(requestingUser, family.get(), memberInvite.getRole(), eventColor);
      family.get().addMember(member);
      familyService.updateFamily(family.get());
      // The code has been used, so remove it
      memberInviteRepository.delete(memberInvite);
    }
  }

  @Override
  public List<MemberInvite> getInvites(Long familyId) {
    if (familyId == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Family id cannot be null.");
    }
    User requestingUser = userService.getRequestingUser();

    Optional<Family> family = familyService.getFamilyById(familyId);
    if (family.isEmpty()) {
      throw new ResourceNotFoundException(
          ApiExceptionCode.FAMILY_DOESNT_EXIST, "Family with id " + familyId + " not found");
    }
    boolean hasAppropriatePermissions =
        familyService.verfiyMinimumRoleSecurity(family.get(), requestingUser, Role.ADULT);
    if (!hasAppropriatePermissions)
      throw new AuthorizationException(
          ApiExceptionCode.USER_PRIVILEGES_TOO_LOW, "User not authorized to complete this action.");

    return memberInviteRepository.getByFamilyId(family.get().getId());
  }
}
