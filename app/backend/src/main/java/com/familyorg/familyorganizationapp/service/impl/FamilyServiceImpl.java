package com.familyorg.familyorganizationapp.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;

import com.familyorg.familyorganizationapp.DTO.FamilyDto;
import com.familyorg.familyorganizationapp.DTO.FamilyMemberDto;
import com.familyorg.familyorganizationapp.DTO.UserDto;
import com.familyorg.familyorganizationapp.DTO.builder.FamilyDtoBuilder;
import com.familyorg.familyorganizationapp.Exception.AuthorizationException;
import com.familyorg.familyorganizationapp.Exception.BadRequestException;
import com.familyorg.familyorganizationapp.Exception.FamilyNotFoundException;
import com.familyorg.familyorganizationapp.Exception.UserNotFoundException;
import com.familyorg.familyorganizationapp.domain.Family;
import com.familyorg.familyorganizationapp.domain.FamilyMemberId;
import com.familyorg.familyorganizationapp.domain.FamilyMembers;
import com.familyorg.familyorganizationapp.domain.Role;
import com.familyorg.familyorganizationapp.domain.User;
import com.familyorg.familyorganizationapp.repository.FamilyMemberRepository;
import com.familyorg.familyorganizationapp.repository.FamilyRepository;
import com.familyorg.familyorganizationapp.service.FamilyService;
import com.familyorg.familyorganizationapp.service.UserService;

@Service
public class FamilyServiceImpl implements FamilyService {
	Logger LOG = LoggerFactory.getLogger(FamilyServiceImpl.class);

	@Autowired
	FamilyRepository familyRepository;

	@Autowired
	FamilyMemberRepository familyMemberRepository;

	@Autowired
	UserService userService;

	@Override
	@Transactional
	public FamilyDto createFamily(FamilyDto familyRequest)
			throws BadRequestException, UserNotFoundException {
		Family family = new Family();
		family.setEventColor(familyRequest.getEventColor());
		family.setName(familyRequest.getName());
		family.setTimezone(familyRequest.getTimezone());
		FamilyMemberDto owner = familyRequest.getOwner();
		User ownerData;
		String errorMessage;
		if (owner.getUsername() != null) {
			ownerData = userService.getUserByUsername(owner.getUsername());
			errorMessage = "Unable to find user with username " + owner.getUsername();
		} else if (owner.getEmail() != null) {
			ownerData = userService.getUserByEmail(owner.getEmail());
			errorMessage = "Unable to find user with email" + owner.getEmail();
		} else {
			throw new BadRequestException("Request must include either username or email of user.");
		}
		if (ownerData == null) {
			throw new UserNotFoundException(errorMessage);
		}
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
		ownerRelation.setUser(ownerData);
		familyMemberRepository.save(ownerRelation);
		FamilyDtoBuilder builder = new FamilyDtoBuilder();
		builder.setId(savedFamily.getId());
		builder.setEventColor(savedFamily.getEventColor());
		builder.setInviteCode(savedFamily.getInviteCode());
		builder.addMember(ownerRelation);
		builder.setName(savedFamily.getName());
		builder.setTimezone(savedFamily.getTimezone());
		builder.setRequestingUser(UserDto.fromUserObj(ownerData));
		return builder.build();
	}

	@Override
	public String generatePermanentFamilyInvite(Long id) {
		// TODO FR.5, FR.7
		return null;
	}

	@Override
	public String generateFamilyInvite(Long id, String recipient) {
		// TODO FR.6, FR.5, FR.7
		return null;
	}

	@Override
	public FamilyDto getFamily(FamilyDto familyRequest)
			throws FamilyNotFoundException {
		Optional<Family> family = familyRepository.findById(familyRequest.getId());


		if (family.isEmpty()) {
			throw new FamilyNotFoundException("Family not found");
		}
		User requestingUser = null;
		if (familyRequest.getRequestingUser().getId() == null) {
			if (familyRequest.getRequestingUser().getUsername() != null) {
				requestingUser = userService.getUserByUsername(
						familyRequest.getRequestingUser().getUsername());
			} else if (familyRequest.getRequestingUser().getEmail() != null) {
				requestingUser = userService.getUserByEmail(
						familyRequest.getRequestingUser().getEmail());
			}
		}
		if (requestingUser == null) {
			requestingUser = userService.getUserById(familyRequest.getRequestingUser().getId());
		}

		if (requestingUser == null) {
			throw new UserNotFoundException("User not found");
		}
		final Long requestingUserId = requestingUser.getId();
		family.get().getMembers().stream()
				.filter(familyMember ->
					familyMember.getUser().getId().equals(
							requestingUserId))
				.findAny()
				.orElseThrow(AuthorizationException::new);
		Family familyObj = family.get();
		return buildFamilyDto(familyObj, requestingUser);
	}

	@Override
	public List<FamilyDto> getFamiliesByUser(Long userId) throws UserNotFoundException {
		User user = userService.getUserById(userId);
		if (user == null) {
			throw new UserNotFoundException("User with id " + userId + " not found");
		}
		List<Family> families = familyRepository.getFamiliesByUserId(userId);
		if (families == null) {
			return Collections.emptyList();
		}

		return families.stream()
				.map(family ->
					buildFamilyDto(family, user))
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public FamilyDto updateFamily(FamilyDto familyRequest)
			throws FamilyNotFoundException, AuthorizationException, UserNotFoundException {
		User user = userService.getUserByUsername(familyRequest.getRequestingUser().getUsername());
		if (user == null) {
			user = userService.getUserById(familyRequest.getRequestingUser().getId());
		}
		if (user == null) {
			user = userService.getUserByEmail(familyRequest.getRequestingUser().getEmail());
		}
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
			family.get().setEventColor(familyRequest.getEventColor());
		}

		Optional<FamilyMembers> familyRelation =
				familyMemberRepository.findById(
						new FamilyMemberId(user.getId(), family.get().getId()));
		if (familyRelation.isEmpty()) {
			throw new AuthorizationException("Unable to find relation to family.");
		} else {
			if (familyRelation.get().getRole().getLevel() >= Role.ADMIN.getLevel()) {
				Family saved = familyRepository.save(family.get());
				return buildFamilyDto(saved, user);
			} else {
				throw new AuthorizationException("You are not authorized to complete this action");
			}
		}
	}

	@Override
	@Transactional
	public void deleteFamily(Long id, String username)
			throws FamilyNotFoundException, AuthorizationException {
		User user = userService.getUserByUsername(username);
		if (user == null) {
			throw new UserNotFoundException("User not found");
		}
		Optional<FamilyMembers> familyRelation =
				familyMemberRepository.findById(
						new FamilyMemberId(user.getId(), id));
		if (familyRelation.isEmpty()) {
			throw new AuthorizationException("Unable to find relation to family.");
		} else {
			if (familyRelation.get().getRole().equals(Role.OWNER)) {
				familyRepository.deleteById(id);
			} else {
				throw new AuthorizationException("You are not authorized to complete this action");
			}
		}
	}

	@Override
	public FamilyDto transferOwnership(Long id, UserDto currentOwner, UserDto newOwner) {
		// TODO FR.9
		return null;
	}

	private FamilyDto buildFamilyDto(Family family, User requestingUser) {
		FamilyDtoBuilder builder = new FamilyDtoBuilder();
		builder.setId(family.getId());
		builder.setInviteCode(family.getInviteCode());
		builder.setEventColor(family.getEventColor());
		builder.setMembers(
				family.getMembers()
					.stream()
					.map(familyMember -> {
						FamilyMemberDto memberDto
							= FamilyMemberDto.fromFamilyMemberObj(familyMember);
						if (memberDto.getRole().equals(Role.OWNER)) {
							builder.setOwner(memberDto);
						}
						return memberDto;
					})
					.collect(Collectors.toSet()));
		builder.setName(family.getName());
		builder.setTimezone(family.getTimezone());
		builder.setRequestingUser(UserDto.fromUserObj(requestingUser));

		return builder.build();
	}

	void setFamilyRepository(FamilyRepository familyRepository) {
		this.familyRepository = familyRepository;
	}

	void setFamilyMemberRepository(FamilyMemberRepository familyMemberRepository) {
		this.familyMemberRepository = familyMemberRepository;
	}

	void setUserService(UserService userService) {
		this.userService = userService;
	}


}
