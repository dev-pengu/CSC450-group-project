package com.familyorg.familyorganizationapp.service;

import com.familyorg.familyorganizationapp.DTO.UserDto;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import com.familyorg.familyorganizationapp.DTO.FamilyDto;
import com.familyorg.familyorganizationapp.DTO.FamilyRoleUpdateRequest;
import com.familyorg.familyorganizationapp.Exception.AuthorizationException;
import com.familyorg.familyorganizationapp.Exception.BadRequestException;
import com.familyorg.familyorganizationapp.Exception.UserNotFoundException;
import com.familyorg.familyorganizationapp.domain.Family;
import com.familyorg.familyorganizationapp.domain.Role;
import com.familyorg.familyorganizationapp.domain.User;

public interface FamilyService {
  FamilyDto createFamily(FamilyDto familyRequest) throws BadRequestException, UserNotFoundException;

  FamilyDto getFamily(Long familyId) throws AuthorizationException;

  Optional<Family> getFamilyById(Long id);

  List<FamilyDto> getFamiliesByUser() throws UserNotFoundException;

  FamilyDto updateFamily(FamilyDto familyRequest)
      throws AuthorizationException, UserNotFoundException;

  Family updateFamily(Family family);

  void deleteFamily(Long id) throws AuthorizationException;

  FamilyDto transferOwnership(FamilyDto request)
      throws AuthorizationException, UserNotFoundException, BadRequestException;

  Family getFamilyByInviteCode(String inviteCode);

  boolean verfiyMinimumRoleSecurity(Family family, User user, Role minimumRole);

  List<Long> getFamilyIdsByUser(String username);

  List<Family> getFamiliesByUser(String username);

  Iterable<Family> findAllByIds(List<Long> familyIds);

  void updateMemberRoles(FamilyRoleUpdateRequest request);

  TimeZone getUserTimeZoneOrDefault(User requestingUser, Family family);

  List<FamilyDto> getFamiliesForFormSelect();

  List<UserDto> getMembersForFormSelect(Long familyId);

  void leaveFamily(Long familyId);

  void removeMember(Long familyId, Long userId);
}
