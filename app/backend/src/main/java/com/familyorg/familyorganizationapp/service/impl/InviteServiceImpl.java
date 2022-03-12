package com.familyorg.familyorganizationapp.service.impl;

import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.familyorg.familyorganizationapp.DTO.FamilyDto;
import com.familyorg.familyorganizationapp.Exception.AuthorizationException;
import com.familyorg.familyorganizationapp.Exception.BadRequestException;
import com.familyorg.familyorganizationapp.Exception.FamilyNotFoundException;
import com.familyorg.familyorganizationapp.Exception.InviteCodeNotFoundException;
import com.familyorg.familyorganizationapp.Exception.UserNotFoundException;
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
  private Logger LOG = LoggerFactory.getLogger(InviteServiceImpl.class);

  @Autowired
  MemberInviteRepository memberInviteRepository;
  @Autowired
  FamilyService familyService;
  @Autowired
  AuthService authService;
  @Autowired
  UserService userService;

  @Override
  @Transactional
  public MemberInvite createUniqueMemberInvite(Long familyId, String userEmail)
      throws FamilyNotFoundException, AuthorizationException {
    Optional<Family> family = familyService.getFamilyById(familyId);
    if (family.isEmpty()) {
      throw new FamilyNotFoundException("Family with id " + familyId + " not found");
    }
    UserDetails userDetails = authService.getSessionUserDetails();
    if (userDetails.getUsername() == null) {
      throw new AuthorizationException("No authenticated user found", true);
    }
    FamilyMembers userMemberEntry = family.get().getMembers().stream()
        .filter(member -> member.getUser().getUsername().equals(userDetails.getUsername())
            && member.getRole().getLevel() >= Role.ADULT.getLevel())
        .findFirst().orElseThrow(AuthorizationException::new);
    MemberInvite invite = new MemberInvite(family.get(), userEmail);
    MemberInvite savedInvite = memberInviteRepository.save(invite);
    return savedInvite;
  }

  @Override
  @Transactional
  public MemberInvite createUniqueMemberInviteWithRole(Long familyId, String userEmail, Role role)
      throws FamilyNotFoundException, AuthorizationException {
    Optional<Family> family = familyService.getFamilyById(familyId);
    if (family.isEmpty()) {
      throw new FamilyNotFoundException("Family with id " + familyId + " not found");
    }
    UserDetails userDetails = authService.getSessionUserDetails();
    if (userDetails.getUsername() == null) {
      throw new AuthorizationException("No authenticated user found", true);
    }
    FamilyMembers userMemberEntry = family.get().getMembers().stream()
        .filter(member -> member.getUser().getUsername().equals(userDetails.getUsername())
            && member.getRole().getLevel() >= Role.ADULT.getLevel())
        .findFirst().orElseThrow(AuthorizationException::new);
    MemberInvite invite = new MemberInvite(family.get(), userEmail, role);
    MemberInvite savedInvite = memberInviteRepository.save(invite);
    return savedInvite;
  }

  @Override
  @Transactional
  public FamilyDto generatePersistentMemberInvite(Long familyId)
      throws FamilyNotFoundException, AuthorizationException {
    Optional<Family> family = familyService.getFamilyById(familyId);
    if (family.isEmpty()) {
      throw new FamilyNotFoundException("Family with id " + familyId + " not found");
    }
    UserDetails userDetails = authService.getSessionUserDetails();
    if (userDetails.getUsername() == null) {
      throw new AuthorizationException("No authenticated user found", true);
    }
    FamilyMembers userMemberEntry = family.get().getMembers().stream()
        .filter(member -> member.getUser().getUsername().equals(userDetails.getUsername())
            && member.getRole().getLevel() >= Role.ADMIN.getLevel())
        .findFirst().orElseThrow(AuthorizationException::new);

    InviteCode inviteCode = new InviteCode(true);
    family.get().setInviteCode(inviteCode);
    Family updatedFamily = familyService.updateFamily(family.get());
    return FamilyDto.fromFamilyObj(updatedFamily,
        userService.getUserByUsername(userDetails.getUsername()));
  }

  @Override
  @Transactional
  public void verifyMemberInvite(InviteCode invite, String eventColor)
      throws AuthorizationException, FamilyNotFoundException, InviteCodeNotFoundException {
    // Get the currently signed in user
    UserDetails userDetails = authService.getSessionUserDetails();
    if (userDetails.getUsername() == null) {
      throw new AuthorizationException("No authenticated user found", true);
    }
    // Get the user object associated with the currently signed in user
    User user = userService.getUserByUsername(userDetails.getUsername());
    if (user == null) {
      throw new UserNotFoundException("User not found with username " + userDetails.getUsername());
    }
    if (!ColorUtil.isValidHexCode(eventColor)) {
      throw new BadRequestException("Event color is not a valid hexcode");
    }
    if (invite.isPersistent()) {
      // Find the family with the persistent invite code
      Family family = familyService.getFamilyByInviteCode(invite.toString());
      if (family == null) {
        throw new InviteCodeNotFoundException("Invalid invite code");
      }
      // Add the user to the family
      FamilyMembers member = new FamilyMembers(user, family, Role.CHILD, eventColor);
      family.addMember(member);
      familyService.updateFamily(family);
    } else {
      // Get the invite data
      MemberInvite memberInvite = memberInviteRepository.findByInviteCode(invite.toString());
      if (memberInvite == null) {
        throw new InviteCodeNotFoundException("Invalid invite code");
      }
      if (!memberInvite.getUserEmail().equals(user.getEmail())) {
        throw new AuthorizationException("You are not authorized to use this invite code", false);
      }
      // Get the family from the invite
      Optional<Family> family = familyService.getFamilyById(memberInvite.getFamilyId());
      if (family.isEmpty()) {
        throw new FamilyNotFoundException("Family not found for invite code " + invite.toString()
            + ". The family may have been removed before you joined.");
      }
      // Add the user to the family
      FamilyMembers member =
          new FamilyMembers(user, family.get(), memberInvite.getRole(), eventColor);
      family.get().addMember(member);
      familyService.updateFamily(family.get());
      // The code has been used, so remove it
      memberInviteRepository.delete(memberInvite);
    }
  }

  @Override
  public List<MemberInvite> getInvites(Long familyId) throws FamilyNotFoundException {
    Optional<Family> family = familyService.getFamilyById(familyId);
    if (family.isEmpty()) {
      throw new FamilyNotFoundException("Family with id " + familyId + " not found");
    }

    return memberInviteRepository.getByFamilyId(family.get().getId());
  }

  /**
   * This should only be called for testing to mock the injected class
   *
   * @param memberInviteRepository
   */
  void setMemberInviteRepository(MemberInviteRepository memberInviteRepository) {
    this.memberInviteRepository = memberInviteRepository;
  }

  /**
   * This should only be called for testing to mock the injected class
   *
   * @param familyService
   */
  void setFamilyService(FamilyService familyService) {
    this.familyService = familyService;
  }

  /**
   * This should only be called for testing to mock the injected class
   *
   * @param authService
   */
  void setAuthService(AuthService authService) {
    this.authService = authService;
  }

  /**
   * This should only be called for testing to mock the injected class
   *
   * @param userService
   */
  void setUserService(UserService userService) {
    this.userService = userService;
  }

}
