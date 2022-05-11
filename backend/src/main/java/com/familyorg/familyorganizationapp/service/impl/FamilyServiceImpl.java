package com.familyorg.familyorganizationapp.service.impl;

import com.familyorg.familyorganizationapp.domain.Poll;
import com.familyorg.familyorganizationapp.domain.ShoppingList;
import com.familyorg.familyorganizationapp.domain.ToDoList;
import com.familyorg.familyorganizationapp.repository.PollOptionRepository;
import com.familyorg.familyorganizationapp.repository.PollRepository;
import com.familyorg.familyorganizationapp.repository.ToDoListRepository;
import com.familyorg.familyorganizationapp.repository.PollVoteRepository;
import com.familyorg.familyorganizationapp.repository.ShoppingListRepository;
import com.familyorg.familyorganizationapp.service.AuthService;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import com.familyorg.familyorganizationapp.DTO.UserDto;
import com.familyorg.familyorganizationapp.DTO.builder.FamilyDtoBuilder;
import com.familyorg.familyorganizationapp.DTO.builder.UserDtoBuilder;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
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

  private Logger logger = LoggerFactory.getLogger(FamilyServiceImpl.class);
  FamilyRepository familyRepository;
  FamilyMemberRepository familyMemberRepository;
  CalendarRepository calendarRepository;
  ShoppingListRepository shoppingListRepository;
  ToDoListRepository toDoListRepository;
  UserService userService;
  AuthService authService;
  PollVoteRepository voteRepository;
  PollRepository pollRepository;
  PollOptionRepository pollOptionRepository;

  @Autowired
  public FamilyServiceImpl(
      FamilyRepository familyRepository,
      FamilyMemberRepository familyMemberRepository,
      CalendarRepository calendarRepository,
      UserService userService,
      ShoppingListRepository shoppingListRepository,
      AuthService authService,
      PollVoteRepository voteRepository,
      ToDoListRepository toDoListRepository,
      PollRepository pollRepository,
      PollOptionRepository pollOptionRepository) {
    this.familyRepository = familyRepository;
    this.familyMemberRepository = familyMemberRepository;
    this.calendarRepository = calendarRepository;
    this.userService = userService;
    this.shoppingListRepository = shoppingListRepository;
    this.toDoListRepository = toDoListRepository;
    this.authService = authService;
    this.voteRepository = voteRepository;
    this.pollRepository = pollRepository;
    this.pollOptionRepository = pollOptionRepository;
  }

  @Override
  @Transactional
  public FamilyDto createFamily(FamilyDto familyRequest)
      throws BadRequestException, UserNotFoundException {
    verifyFamilyCreationRequest(familyRequest);
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

    ShoppingList shoppingList = new ShoppingList();
    shoppingList.setDefault(true);
    shoppingList.setDescription("Family Shopping List");
    shoppingList.setFamily(family);
    shoppingList.setCreatedDatetime(Timestamp.from(Instant.now()));
    shoppingList.setCreatedBy(ownerUser);
    ShoppingList savedShoppingList = shoppingListRepository.save(shoppingList);
    savedFamily.addShoppingList(savedShoppingList);

    ToDoList list = new ToDoList();
    list.setDefaultList(true);
    list.setDescription("Family To-Do List");
    list.setFamily(family);
    list.setCreatedDatetime(Timestamp.from(Instant.now()));
    list.setCreatedBy(ownerUser);
    ToDoList savedList = toDoListRepository.save(list);
    savedFamily.addToDoList(savedList);

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
  public FamilyDto getFamily(Long familyId) {
    if (familyId == null) {
      throw new BadRequestException(ApiExceptionCode.REQUIRED_PARAM_MISSING, "Id cannot be null.");
    }
    Optional<Family> family = familyRepository.findById(familyId);

    if (family.isEmpty()) {
      throw new ResourceNotFoundException(
          ApiExceptionCode.FAMILY_DOESNT_EXIST, "Family with id " + familyId + " not found");
    }

    User requestingUser = userService.getRequestingUser();
    boolean hasAppropriatePermissions =
        verfiyMinimumRoleSecurity(family.get(), requestingUser, Role.CHILD);
    if (!hasAppropriatePermissions) {
      throw new AuthorizationException(
          ApiExceptionCode.USER_PRIVILEGES_TOO_LOW, "User not authorized to complete this action.");
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
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Id field is required to update a family.");
    }
    if (familyRequest.getEventColor() != null
        && !ColorUtil.isValidHexCode(familyRequest.getEventColor())) {
      throw new BadRequestException(
          ApiExceptionCode.BAD_PARAM_VALUE, "Family event color is not a valid hexcode.");
    }
    User requestingUser = userService.getRequestingUser();

    Optional<Family> family = familyRepository.findById(familyRequest.getId());
    if (family.isEmpty()) {
      throw new ResourceNotFoundException(
          ApiExceptionCode.FAMILY_DOESNT_EXIST,
          "Family with id " + familyRequest.getId() + " not found.");
    }
    boolean hasAppropriatePermissions =
        verfiyMinimumRoleSecurity(family.get(), requestingUser, Role.ADMIN);
    if (!hasAppropriatePermissions) {
      throw new AuthorizationException(
          ApiExceptionCode.USER_PRIVILEGES_TOO_LOW, "User not authorized to complete this action.");
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
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Id is required to delete a family.");
    }
    User requestingUser = userService.getRequestingUser();
    Optional<Family> family = familyRepository.findById(id);
    if (family.isEmpty()) {
      throw new ResourceNotFoundException(
          ApiExceptionCode.FAMILY_DOESNT_EXIST, "Family with id " + id + " not found.");
    }
    boolean hasAppropriatePermissions =
        verfiyMinimumRoleSecurity(family.get(), requestingUser, Role.OWNER);
    if (!hasAppropriatePermissions) {
      throw new AuthorizationException(
          ApiExceptionCode.USER_PRIVILEGES_TOO_LOW, "User not authorized to complete this action.");
    }
    List<Poll> polls =
        pollRepository.findByFamily(family.get());
    logger.info(polls.toString());
    for (Poll poll : polls) {
      voteRepository.deleteByPollId(poll.getId());
      pollOptionRepository.deleteByPollId(poll.getId());
      poll.setOptions(null);
      poll.setRespondents(null);
      pollRepository.delete(poll);
    }
    family.get().setPoll(null);
    familyRepository.delete(family.get());
  }

  @Transactional
  @Override
  public FamilyDto transferOwnership(FamilyDto request) {
    if (request.getId() == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Family id cannot be null.");
    }
    if (request.getOwner() == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "A user to transfer to is required.");
    }
    User requestingUser = userService.getRequestingUser();
    Optional<Family> family = familyRepository.findById(request.getId());
    if (family.isEmpty()) {
      throw new ResourceNotFoundException(
          ApiExceptionCode.FAMILY_DOESNT_EXIST,
          "Family with id " + request.getId() + " not found.");
    }
    boolean hasAppropriatePermissions =
        verfiyMinimumRoleSecurity(family.get(), requestingUser, Role.OWNER);
    if (!hasAppropriatePermissions) {
      throw new AuthorizationException(
          ApiExceptionCode.USER_PRIVILEGES_TOO_LOW, "User not authorized to complete this action.");
    }
    Optional<FamilyMembers> currentOwnerRelation =
        family.get().getMembers().stream()
            .filter(member -> member.getRole() == Role.OWNER)
            .findFirst();

    // if owner on request is the same as the current owner, no work needs to be done, just return
    // the current family object
    if (request.getOwner().getUser().getUsername().equals(requestingUser.getUsername())) {
      return FamilyDto.fromFamilyObj(family.get(), requestingUser);
    }

    User newOwner = userService.getUserByUsername(request.getOwner().getUser().getUsername());
    if (newOwner == null) {
      throw new UserNotFoundException(
          ApiExceptionCode.USER_DOESNT_EXIST,
          "User supplied to be new owner does not exist.",
          false);
    }

    Optional<FamilyMembers> newOwnerRelation =
        family.get().getMembers().stream()
            .filter(
                member ->
                    Objects.equals(member.getUser().getId(), request.getOwner().getUser().getId()))
            .findFirst();
    if (newOwnerRelation.isEmpty()) {
      throw new UserNotFoundException(
          ApiExceptionCode.USER_NOT_IN_FAMILY,
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
    if (request.getFamilyId() == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Family id cannot be null.");
    }
    if (request.getMembers() == null || request.getMembers().isEmpty()) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "No members were supplied to update.");
    }
    User requestingUser = userService.getRequestingUser();
    if (request.getFamilyId() == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Family id may not be null.");
    }
    if (request.getMembers() == null || request.getMembers().isEmpty()) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Family members to update must be specified.");
    }
    Optional<Family> family = familyRepository.findById(request.getFamilyId());
    if (family.isEmpty()) {
      throw new ResourceNotFoundException(
          ApiExceptionCode.FAMILY_DOESNT_EXIST,
          "Family with id " + request.getFamilyId() + " not found.");
    }
    boolean hasAppropriatePermissions =
        verfiyMinimumRoleSecurity(family.get(), requestingUser, Role.ADMIN);
    if (!hasAppropriatePermissions) {
      throw new AuthorizationException(
          ApiExceptionCode.USER_PRIVILEGES_TOO_LOW, "User not authorized to complete this action.");
    }
    Set<FamilyMembers> membersToUpdate = new HashSet<>();
    request
        .getMembers()
        .forEach(
            member -> {
              FamilyMemberId id =
                  new FamilyMemberId(member.getUser().getId(), family.get().getId());
              Optional<FamilyMembers> familyMemberOpt = familyMemberRepository.findById(id);
              if (familyMemberOpt.isEmpty()) {
                throw new UserNotFoundException(
                    ApiExceptionCode.USER_DOESNT_EXIST,
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

  @Override
  public List<FamilyDto> getFamiliesForFormSelect() {
    User requestingUser = userService.getRequestingUser();
    List<Family> families = getFamiliesByUser(requestingUser.getUsername());
    return families.stream()
        .map(
            family ->
                new FamilyDtoBuilder().withId(family.getId()).withName(family.getName()).build())
        .collect(Collectors.toList());
  }

  @Override
  public List<UserDto> getMembersForFormSelect(Long familyId) {
    if (familyId == null) {
      throw new BadRequestException(ApiExceptionCode.REQUIRED_PARAM_MISSING, "Id cannot be null.");
    }
    User requestingUser = userService.getRequestingUser();
    Optional<Family> family = familyRepository.findById(familyId);
    if (family.isEmpty()) {
      throw new ResourceNotFoundException(
          ApiExceptionCode.FAMILY_DOESNT_EXIST, "Family with id " + familyId + " not found.");
    }
    boolean hasAppropriatePermissions =
        verfiyMinimumRoleSecurity(family.get(), requestingUser, Role.CHILD);
    if (!hasAppropriatePermissions) {
      throw new AuthorizationException(
          ApiExceptionCode.USER_PRIVILEGES_TOO_LOW, "User not authorized to complete this action.");
    }

    return family.get().getMembers().stream()
        .map(
            member ->
                new UserDtoBuilder()
                    .withId(member.getUser().getId())
                    .withFirstName(member.getUser().getFirstName())
                    .withLastName(member.getUser().getLastName())
                    .build())
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void leaveFamily(Long familyId) {
    if (familyId == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Family id must not be null");
    }
    User requestingUser = userService.getRequestingUser();
    if (authService.hasAuthenticatedForSensitiveActions(requestingUser.getUsername())) {
      Optional<FamilyMembers> memberRecord =
          familyMemberRepository.findById(new FamilyMemberId(requestingUser.getId(), familyId));
      if (memberRecord.isEmpty()) {
        throw new AuthorizationException(
            ApiExceptionCode.USER_NOT_IN_FAMILY, "User is not a member of the requested family.");
      }
      if (memberRecord.get().getRole().equals(Role.OWNER)) {
        throw new AuthorizationException(
            ApiExceptionCode.ACTION_NOT_PERMITTED,
            "User cannot leave a family they own. The family ownership must first be transferred.");
      }
      // remove all events assigned to the user
      requestingUser.getEvents().removeIf(event -> event.getCalendar().getFamily().getId().equals(familyId));
      // remove any outstanding poll responses from the user and any open polls
      voteRepository.deleteAll(
        voteRepository.getVotesForDeletionByUserAndFamily(requestingUser.getId(), familyId));
      // remove the family member record
      requestingUser.getFamilies().removeIf(family -> family.getFamily().getId().equals(familyId));
      userService.updateUser(requestingUser);
    } else {
      throw new AuthorizationException(
          ApiExceptionCode.REAUTHENTICATION_NEEDED_FOR_REQUEST,
          "Users must reauthenticate to perform this action.");
    }
  }

  @Override
  @Transactional
  public void removeMember(Long familyId, Long userId) {
    if (familyId == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Family id must not be null");
    }
    if (userId == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "User id must not be null");
    }
    User requestingUser = userService.getRequestingUser();
    if (authService.hasAuthenticatedForSensitiveActions(requestingUser.getUsername())) {
      Optional<FamilyMembers> memberRecord =
          familyMemberRepository.findById(new FamilyMemberId(requestingUser.getId(), familyId));
      if (memberRecord.isEmpty()) {
        throw new AuthorizationException(
            ApiExceptionCode.USER_NOT_IN_FAMILY, "User is not a member of the requested family.");
      }
      if (!verfiyMinimumRoleSecurity(memberRecord.get().getFamily(), requestingUser, Role.ADMIN)) {
        throw new AuthorizationException(
            ApiExceptionCode.ACTION_NOT_PERMITTED,
            "User does not have permissions to remove a member from the specified family.");
      }
      Optional<FamilyMembers> memberRecordToRemove =
          familyMemberRepository.findById(new FamilyMemberId(userId, familyId));
      if (memberRecordToRemove.isEmpty()) {
        throw new AuthorizationException(
            ApiExceptionCode.USER_NOT_IN_FAMILY,
            "User to be removed is not a member of the requested family.");
      }

      // remove all events assigned to the user
      memberRecordToRemove.get().getUser().getEvents().removeIf(event -> event.getCalendar().getFamily().getId().equals(familyId));
      // remove any outstanding poll responses from the user and any open polls
      voteRepository.deleteAll(
        voteRepository.getVotesForDeletionByUserAndFamily(memberRecordToRemove.get().getUser().getId(), familyId));

      // remove the family member record
      memberRecordToRemove
          .get()
          .getUser()
          .getFamilies()
          .removeIf(family -> family.getFamily().getId().equals(familyId));
      userService.updateUser(memberRecordToRemove.get().getUser());
    } else {
      throw new AuthorizationException(
          ApiExceptionCode.REAUTHENTICATION_NEEDED_FOR_REQUEST,
          "Users must reauthenticate to perform this action.");
    }
  }

  /**
   * Methods below should only be called from other services and null checking should be used as it
   * is not guaranteed a result will be returned.
   */
  @Override
  public Family getFamilyByInviteCode(String inviteCode) {
    Objects.requireNonNull(inviteCode);
    return familyRepository.findByInviteCode(inviteCode);
  }

  @Override
  public boolean verfiyMinimumRoleSecurity(Family family, User user, Role minimumRole) {
    Objects.requireNonNull(family);
    Objects.requireNonNull(user);
    Objects.requireNonNull(minimumRole);
    Optional<FamilyMembers> memberRecord =
        family.getMembers().stream()
            .filter(member -> member.getUser().getUsername().equals(user.getUsername()))
            .findFirst();
    if (memberRecord.isPresent()) {
      return memberRecord.get().getRole().getLevel() >= minimumRole.getLevel();
    } else {
      throw new AuthorizationException(
          ApiExceptionCode.USER_NOT_IN_FAMILY,
          "User is not a member of the family owning thee requesting resource.");
    }
  }

  @Override
  public TimeZone getUserTimeZoneOrDefault(User requestingUser, Family family) {
    Objects.requireNonNull(requestingUser);
    Objects.requireNonNull(family);
    return requestingUser.getTimezone() != null
        ? TimeZone.getTimeZone(requestingUser.getTimezone())
        : TimeZone.getTimeZone(family.getTimezone());
  }

  @Override
  public List<Long> getFamilyIdsByUser(String username) {
    Objects.requireNonNull(username);
    return familyRepository.getFamilyIdsByUser(username);
  }

  @Override
  public List<Family> getFamiliesByUser(String username) {
    Objects.requireNonNull(username);
    return familyRepository.getFamiliesByUser(username);
  }

  @Override
  public Iterable<Family> findAllByIds(List<Long> familyIds) {
    Objects.requireNonNull(familyIds);
    return familyRepository.findAllById(familyIds);
  }

  @Override
  @Transactional
  public Family updateFamily(Family family) {
    Objects.requireNonNull(family);
    return familyRepository.save(family);
  }

  @Override
  public Optional<Family> getFamilyById(Long id) {
    Objects.requireNonNull(id);
    return familyRepository.findById(id);
  }

  private void verifyFamilyCreationRequest(FamilyDto request) throws BadRequestException {
    if (request.getName() == null || request.getName().isBlank()) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Family name must not be null.");
    }
    if (request.getTimezone() == null
        || request.getTimezone().isBlank()
        || !ZoneId.getAvailableZoneIds().contains(request.getTimezone())) {
      throw new BadRequestException(
          ApiExceptionCode.BAD_PARAM_VALUE, "Family event timezone must be a valid timezone.");
    }
    if (request.getEventColor() == null
        || request.getEventColor().isBlank()
        || !ColorUtil.isValidHexCode(request.getEventColor())) {
      throw new BadRequestException(
          ApiExceptionCode.BAD_PARAM_VALUE, "Family event color is not a valid hex code");
    }
    if (request.getOwner() == null
        || request.getOwner().getEventColor() == null
        || request.getOwner().getEventColor().isBlank()
        || !ColorUtil.isValidHexCode(request.getOwner().getEventColor())) {
      throw new BadRequestException(
          ApiExceptionCode.BAD_PARAM_VALUE, "Owner event color is not a valid hex code.");
    }
  }
}
