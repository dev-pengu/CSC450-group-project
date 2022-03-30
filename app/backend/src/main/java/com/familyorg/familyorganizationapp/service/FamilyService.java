package com.familyorg.familyorganizationapp.service;

import java.util.List;
import java.util.Optional;
import com.familyorg.familyorganizationapp.DTO.FamilyDto;
import com.familyorg.familyorganizationapp.DTO.FamilyRoleUpdateRequest;
import com.familyorg.familyorganizationapp.Exception.AuthorizationException;
import com.familyorg.familyorganizationapp.Exception.BadRequestException;
import com.familyorg.familyorganizationapp.Exception.FamilyNotFoundException;
import com.familyorg.familyorganizationapp.Exception.UserNotFoundException;
import com.familyorg.familyorganizationapp.domain.Family;
import com.familyorg.familyorganizationapp.domain.Role;
import com.familyorg.familyorganizationapp.domain.User;

public interface FamilyService {
  FamilyDto createFamily(FamilyDto familyRequest) throws BadRequestException, UserNotFoundException;

  FamilyDto getFamily(FamilyDto familyRequest)
      throws FamilyNotFoundException, AuthorizationException;

  Optional<Family> getFamilyById(Long id);

  List<FamilyDto> getFamiliesByUser(Long userId) throws UserNotFoundException;

  FamilyDto updateFamily(FamilyDto familyRequest)
      throws AuthorizationException, FamilyNotFoundException, UserNotFoundException;

  Family updateFamily(Family family);

  void deleteFamily(Long id) throws AuthorizationException;

  FamilyDto transferOwnership(FamilyDto request) throws AuthorizationException,
      UserNotFoundException, FamilyNotFoundException, BadRequestException;

  Family getFamilyByInviteCode(String inviteCode);

  boolean verfiyMinimumRoleSecurity(Family family, User user, Role minimumRole);

  List<Long> getFamilyIdsByUser(String username);

  List<Family> getFamiliesByUser(String username);

  void updateMemberRoles(FamilyRoleUpdateRequest request);
}
