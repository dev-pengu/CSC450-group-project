package com.familyorg.familyorganizationapp.service.impl;

import com.familyorg.familyorganizationapp.DTO.FamilyDto;
import com.familyorg.familyorganizationapp.Exception.AuthorizationException;
import com.familyorg.familyorganizationapp.Exception.FamilyNotFoundException;
import com.familyorg.familyorganizationapp.domain.Family;
import com.familyorg.familyorganizationapp.domain.FamilyMembers;
import com.familyorg.familyorganizationapp.domain.InviteCode;
import com.familyorg.familyorganizationapp.domain.MemberInvite;
import com.familyorg.familyorganizationapp.domain.Role;
import com.familyorg.familyorganizationapp.repository.FamilyRepository;
import com.familyorg.familyorganizationapp.repository.MemberInviteRepository;
import com.familyorg.familyorganizationapp.service.AuthService;
import com.familyorg.familyorganizationapp.service.FamilyService;
import com.familyorg.familyorganizationapp.service.InviteService;
import com.familyorg.familyorganizationapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

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
    if (userDetails == null) {
      throw new AuthorizationException("No authenticated user found");
    }
    FamilyMembers userMemberEntry = family.get()
        .getMembers()
        .stream()
        .filter(member -> member.getUser().getUsername().equals(userDetails.getUsername()) &&
            member.getRole().getLevel() >= Role.ADULT.getLevel())
        .findFirst()
        .orElseThrow(AuthorizationException::new);
    MemberInvite invite = new MemberInvite(family.get(), userEmail);
    MemberInvite savedInvite = memberInviteRepository.save(invite);
    return savedInvite;
  }

  @Override
  public MemberInvite createUniqueMemberInviteWithRole(Long familyId, String userEmail, Role role)
      throws FamilyNotFoundException, AuthorizationException {
    Optional<Family> family = familyService.getFamilyById(familyId);
    if (family.isEmpty()) {
      throw new FamilyNotFoundException("Family with id " + familyId + " not found");
    }
    UserDetails userDetails = authService.getSessionUserDetails();
    if (userDetails == null) {
      throw new AuthorizationException("No authenticated user found");
    }
    FamilyMembers userMemberEntry = family.get()
        .getMembers()
        .stream()
        .filter(member -> member.getUser().getUsername().equals(userDetails.getUsername()) &&
            member.getRole().getLevel() >= Role.ADULT.getLevel())
        .findFirst()
        .orElseThrow(AuthorizationException::new);
    MemberInvite invite = new MemberInvite(family.get(), userEmail, role);
    MemberInvite savedInvite = memberInviteRepository.save(invite);
    return savedInvite;
  }

  @Override
  public FamilyDto generatePersistentMemberInvite(Long familyId)
      throws FamilyNotFoundException, AuthorizationException {
    Optional<Family> family = familyService.getFamilyById(familyId);
    if (family.isEmpty()) {
      throw new FamilyNotFoundException("Family with id " + familyId + " not found");
    }
    UserDetails userDetails = authService.getSessionUserDetails();
    if (userDetails == null) {
      throw new AuthorizationException("No authenticated user found");
    }
    FamilyMembers userMemberEntry = family.get()
        .getMembers()
        .stream()
        .filter(member ->
            member
                .getUser()
                .getUsername()
                .equals(
                    userDetails.getUsername()) &&
                    member.getRole().getLevel() >= Role.ADMIN.getLevel())
        .findFirst()
        .orElseThrow(AuthorizationException::new);

    InviteCode inviteCode = new InviteCode(true);
    family.get().setInviteCode(inviteCode);
    Family updatedFamily = familyService.updateFamily(family.get());
    return FamilyDto.fromFamilyObj(updatedFamily, userService.getUserByUsername(userDetails.getUsername()));
  }

  @Override
  public MemberInvite verifyMemberInvite(MemberInvite memberInvite, boolean isPersistentCode) {
    return null;
  }
/**
 * This should only be called for testing to mock the injected class
 * @param memberInviteRepository
 */
	void setMemberInviteRepository(MemberInviteRepository memberInviteRepository) {
		this.memberInviteRepository = memberInviteRepository;
	}
	/**
	 * This should only be called for testing to mock the injected class
	 * @param familyService
	 */
	void setFamilyService(FamilyService familyService) {
		this.familyService = familyService;
	}
	/**
	 * This should only be called for testing to mock the injected class
	 * @param authService
	 */
	void setAuthService(AuthService authService) {
		this.authService = authService;
	}
	/**
	 * This should only be called for testing to mock the injected class
	 * @param userService
	 */
	void setUserService(UserService userService) {
		this.userService = userService;
	}


}
