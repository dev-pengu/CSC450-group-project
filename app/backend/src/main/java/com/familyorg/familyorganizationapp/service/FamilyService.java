package com.familyorg.familyorganizationapp.service;

import java.util.List;

import com.familyorg.familyorganizationapp.DTO.FamilyDto;
import com.familyorg.familyorganizationapp.DTO.UserDto;
import com.familyorg.familyorganizationapp.Exception.AuthorizationException;
import com.familyorg.familyorganizationapp.Exception.BadRequestException;
import com.familyorg.familyorganizationapp.Exception.FamilyNotFoundException;
import com.familyorg.familyorganizationapp.Exception.UserNotFoundException;

public interface FamilyService {
	FamilyDto createFamily(FamilyDto familyRequest) throws BadRequestException, UserNotFoundException;
	String generatePermanentFamilyInvite(Long id);
	String generateFamilyInvite(Long id, String recipient);
	FamilyDto getFamily(FamilyDto familyRequest) throws FamilyNotFoundException, AuthorizationException;
	List<FamilyDto> getFamiliesByUser(Long userId) throws UserNotFoundException;
	FamilyDto updateFamily(FamilyDto familyRequest) throws AuthorizationException, FamilyNotFoundException, UserNotFoundException;
	void deleteFamily(Long id, String username) throws AuthorizationException;
	FamilyDto transferOwnership(Long id, UserDto currentOwner, UserDto newOwner);
}
