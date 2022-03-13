package com.familyorg.familyorganizationapp.service;

import java.util.List;
import java.util.Optional;
import com.familyorg.familyorganizationapp.DTO.FamilyDto;
import com.familyorg.familyorganizationapp.DTO.UserDto;
import com.familyorg.familyorganizationapp.Exception.AuthorizationException;
import com.familyorg.familyorganizationapp.Exception.BadRequestException;
import com.familyorg.familyorganizationapp.Exception.FamilyNotFoundException;
import com.familyorg.familyorganizationapp.Exception.UserNotFoundException;
import com.familyorg.familyorganizationapp.domain.Family;

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

  FamilyDto transferOwnership(Long id, UserDto currentOwner, UserDto newOwner);

  Family getFamilyByInviteCode(String inviteCode);
}
