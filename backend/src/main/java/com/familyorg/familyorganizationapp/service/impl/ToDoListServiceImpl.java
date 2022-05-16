package com.familyorg.familyorganizationapp.service.impl;

import com.familyorg.familyorganizationapp.DTO.ToDoListSearchRequestDto;
import com.familyorg.familyorganizationapp.DTO.ToDoListSearchResponseDto;
import com.familyorg.familyorganizationapp.DTO.UserDto;
import com.familyorg.familyorganizationapp.DTO.builder.ToDoListDtoBuilder;
import com.familyorg.familyorganizationapp.DTO.builder.ToDoTaskDtoBuilder;
import com.familyorg.familyorganizationapp.domain.search.SearchFilter;
import com.familyorg.familyorganizationapp.domain.search.ToDoField;
import com.familyorg.familyorganizationapp.util.DateUtil;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.DurationFieldType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.familyorg.familyorganizationapp.DTO.ToDoListDto;
import com.familyorg.familyorganizationapp.DTO.ToDoTaskDto;
import com.familyorg.familyorganizationapp.Exception.AuthorizationException;
import com.familyorg.familyorganizationapp.Exception.BadRequestException;
import com.familyorg.familyorganizationapp.Exception.ResourceNotFoundException;
import com.familyorg.familyorganizationapp.Exception.UserNotFoundException;
import com.familyorg.familyorganizationapp.Exception.ApiExceptionCode;
import com.familyorg.familyorganizationapp.domain.Family;
import com.familyorg.familyorganizationapp.domain.Role;
import com.familyorg.familyorganizationapp.domain.ToDoList;
import com.familyorg.familyorganizationapp.domain.ToDoTask;
import com.familyorg.familyorganizationapp.domain.User;
import com.familyorg.familyorganizationapp.repository.ToDoListRepository;
import com.familyorg.familyorganizationapp.repository.ToDoTaskRepository;
import com.familyorg.familyorganizationapp.service.FamilyService;
import com.familyorg.familyorganizationapp.service.ToDoListService;
import com.familyorg.familyorganizationapp.service.UserService;

@Service
public class ToDoListServiceImpl implements ToDoListService {
  private final Logger logger = LoggerFactory.getLogger(ToDoListServiceImpl.class);

  ToDoListRepository toDoListRepository;
  ToDoTaskRepository toDoTaskRepository;
  UserService userService;
  FamilyService familyService;

  @Autowired
  public ToDoListServiceImpl(
      ToDoListRepository toDoListRepository,
      ToDoTaskRepository toDoTaskRepository,
      UserService userService,
      FamilyService familyService) {
    this.toDoListRepository = toDoListRepository;
    this.toDoTaskRepository = toDoTaskRepository;
    this.userService = userService;
    this.familyService = familyService;
  }

  @Override
  @Transactional
  public void createList(ToDoListDto request)
      throws UserNotFoundException, AuthorizationException, ResourceNotFoundException {
    if (request.getDescription() == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "List description must not be null.");
    }
    if (request.getFamilyId() == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING,
          "List must have a family id to associate the list with.");
    }

    User requestingUser = userService.getRequestingUser();
    Optional<Family> family = familyService.getFamilyById(request.getFamilyId());

    if (family.isEmpty()) {
      throw new ResourceNotFoundException(
          ApiExceptionCode.FAMILY_DOESNT_EXIST,
          "Family with id " + request.getFamilyId() + " not found.");
    }

    boolean hasAppropriatePermissions =
        familyService.verfiyMinimumRoleSecurity(family.get(), requestingUser, Role.ADULT);

    if (!hasAppropriatePermissions) {
      throw new AuthorizationException(
          ApiExceptionCode.USER_PRIVILEGES_TOO_LOW, "User not authorized to complete this action.");
    }

    ToDoList toDoList = new ToDoList();
    toDoList.setDescription(request.getDescription());
    toDoList.setFamily(family.get());
    toDoList.setDefaultList(false);
    toDoList.setCreatedBy(requestingUser);
    toDoList.setCreatedDatetime(Timestamp.from(Instant.now()));
    toDoListRepository.save(toDoList);
  }

  @Override
  @Transactional
  public void updateList(ToDoListDto request)
      throws UserNotFoundException, AuthorizationException, ResourceNotFoundException {
    if (request.getId() == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "List id must not be null.");
    }
    Optional<ToDoList> toDoListOpt = toDoListRepository.findById(request.getId());
    if (toDoListOpt.isEmpty()) {
      throw new ResourceNotFoundException(
          ApiExceptionCode.LIST_DOESNT_EXIST, "List with id " + request.getId() + " not found.");
    }
    User requestingUser = userService.getRequestingUser();

    boolean hasAppropriatePermissions =
        familyService.verfiyMinimumRoleSecurity(
            toDoListOpt.get().getFamily(), requestingUser, Role.ADMIN);
    if (!hasAppropriatePermissions) {
      hasAppropriatePermissions =
          toDoListOpt.get().getCreatedBy().getId().equals(requestingUser.getId());
    }
    if (!hasAppropriatePermissions) {
      throw new AuthorizationException(
          ApiExceptionCode.USER_PRIVILEGES_TOO_LOW, "User not authorized to complete this action.");
    }

    ToDoList toDoList = toDoListOpt.get();

    if (toDoList.getDescription() != request.getDescription()) {
      toDoList.setDescription(request.getDescription());
    }
    // Save to do list item to the repository
    toDoListRepository.save(toDoList);
  }

  @Override
  @Transactional
  public void deleteList(Long id)
      throws UserNotFoundException, AuthorizationException, ResourceNotFoundException {
    if (id == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Id must not be null.");
    }
    Optional<ToDoList> toDoList = toDoListRepository.findById(id);
    if (toDoList.isEmpty()) {
      throw new ResourceNotFoundException(
          ApiExceptionCode.LIST_DOESNT_EXIST, "To do list with id " + id + " not found.");
    }
    User requestingUser = userService.getRequestingUser();

    boolean hasAppropriatePermissions =
        familyService.verfiyMinimumRoleSecurity(
            toDoList.get().getFamily(), requestingUser, Role.ADMIN);
    if (!hasAppropriatePermissions) {
      hasAppropriatePermissions =
          toDoList.get().getCreatedBy().getId().equals(requestingUser.getId());
    }
    if (!hasAppropriatePermissions) {
      throw new AuthorizationException(
          ApiExceptionCode.USER_PRIVILEGES_TOO_LOW, "User not authorized to complete this action.");
    }
    if (toDoList.get().isDefault()) {
      throw new BadRequestException(
          ApiExceptionCode.ILLEGAL_ACTION_REQUESTED,
          "The family's default to do list cannot be deleted.");
    }
    toDoListRepository.delete(toDoList.get());
  }

  @Override
  public ToDoListDto getToDoList(Long id) {
    if (id == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Id must not be null.");
    }
    Optional<ToDoList> toDoList = toDoListRepository.findById(id);
    if (toDoList.isEmpty()) {
      throw new ResourceNotFoundException(
          ApiExceptionCode.LIST_DOESNT_EXIST, "To do list with id " + id + " not found.");
    }
    User requestingUser = userService.getRequestingUser();
    boolean hasAppropriatePermissions =
        familyService.verfiyMinimumRoleSecurity(
            toDoList.get().getFamily(), requestingUser, Role.CHILD);
    if (!hasAppropriatePermissions) {
      throw new AuthorizationException(
          ApiExceptionCode.USER_PRIVILEGES_TOO_LOW, "User not authorized to complete this action.");
    }

    TimeZone timezone =
        requestingUser.getTimezone() != null
            ? TimeZone.getTimeZone(requestingUser.getTimezone())
            : TimeZone.getTimeZone(toDoList.get().getFamily().getTimezone());
    return new ToDoListDtoBuilder()
        .setId(toDoList.get().getId())
        .setDescription(toDoList.get().getDescription())
        .setIsDefault(toDoList.get().isDefault())
        .setColor(toDoList.get().getFamily().getEventColor())
        .setCreatedBy(UserDto.fromUserObj(toDoList.get().getCreatedBy()))
        .setCreated(
            DateUtil.toTimezone(
                toDoList.get().getCreatedDatetime(), TimeZone.getDefault(), timezone))
        .setFamilyId(toDoList.get().getFamily().getId())
        .setTasks(
            toDoList.get().getItems().stream()
                .map(
                    item ->
                        new ToDoTaskDtoBuilder()
                            .setId(item.getId())
                            .setDescription(item.getDescription())
                            .setDueDate(item.getDueDate())
                            .setAddedBy(UserDto.fromUserObj(item.getAddedBy()))
                            .setCreated(
                                DateUtil.toTimezone(
                                    item.getCreatedDatetime(), TimeZone.getDefault(), timezone))
                            .setCompleted(item.getCompletedDatetime() != null)
                            .setCompletedDateTime(item.getCompletedDatetime())
                            .setNotes(item.getNotes())
                            .setListId(item.getList().getId())
                            .setOverdue(
                                item.getDueDate() != null
                                    && DateTimeComparator.getDateOnlyInstance()
                                            .compare(item.getDueDate(), DateTime.now())
                                        < 0)
                            .setDueNextTwoDays(
                                item.getDueDate() != null
                                    && DateTimeComparator.getDateOnlyInstance()
                                            .compare(
                                                item.getDueDate(),
                                                DateTime.now()
                                                    .withFieldAdded(DurationFieldType.days(), 2))
                                        < 0)
                            .build())
                .collect(Collectors.toList()))
        .build();
  }

  @Override
  public ToDoTaskDto getTask(Long id)
      throws UserNotFoundException, AuthorizationException, ResourceNotFoundException {
    if (id == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Item id must not be null.");
    }
    Optional<ToDoTask> toDoTask = toDoTaskRepository.findById(id);
    if (toDoTask.isEmpty()) {
      throw new ResourceNotFoundException(
          ApiExceptionCode.LIST_ITEM_DOESNT_EXIST, "List item with id " + id + " not found.");
    }
    User requestingUser = userService.getRequestingUser();
    boolean hasAppropriatePermissions =
        familyService.verfiyMinimumRoleSecurity(
            toDoTask.get().getList().getFamily(), requestingUser, Role.CHILD);
    if (!hasAppropriatePermissions) {
      throw new AuthorizationException(
          ApiExceptionCode.USER_PRIVILEGES_TOO_LOW, "User not authorized to complete this action.");
    }

    TimeZone timezone =
        requestingUser.getTimezone() != null
            ? TimeZone.getTimeZone(requestingUser.getTimezone())
            : TimeZone.getTimeZone(toDoTask.get().getList().getFamily().getTimezone());

    return new ToDoTaskDtoBuilder()
        .setId(toDoTask.get().getId())
        .setDescription(toDoTask.get().getDescription())
        .setDueDate(toDoTask.get().getDueDate())
        .setAddedBy(UserDto.fromUserObj(toDoTask.get().getAddedBy()))
        .setCreated(
            DateUtil.toTimezone(
                toDoTask.get().getCreatedDatetime(), TimeZone.getDefault(), timezone))
        .setCompleted(toDoTask.get().getCompletedDatetime() != null)
        .setCompletedDateTime(toDoTask.get().getCompletedDatetime())
        .setNotes(toDoTask.get().getNotes())
        .setListId(toDoTask.get().getList().getId())
        .setOverdue(
            toDoTask.get().getDueDate() != null
                && DateTimeComparator.getDateOnlyInstance()
                        .compare(toDoTask.get().getDueDate(), DateTime.now())
                    < 0)
        .setDueNextTwoDays(
            toDoTask.get().getDueDate() != null
                && DateTimeComparator.getDateOnlyInstance()
                        .compare(
                            toDoTask.get().getDueDate(),
                            DateTime.now().withFieldAdded(DurationFieldType.days(), 2))
                    < 0)
        .build();
  }

  @Override
  @Transactional
  public void addTask(ToDoTaskDto request)
      throws UserNotFoundException, AuthorizationException, ResourceNotFoundException {
    if (request.getDescription() == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Item description must not be null.");
    }
    if (request.getListId() == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING,
          "A list id must be specified to create a list item.");
    }
    User requestingUser = userService.getRequestingUser();
    Optional<ToDoList> list = toDoListRepository.findById(request.getListId());
    if (list.isEmpty()) {
      throw new ResourceNotFoundException(
          ApiExceptionCode.LIST_DOESNT_EXIST,
          "List with id " + request.getListId() + " not found.");
    }

    boolean hasAppropriatePermissions =
        familyService.verfiyMinimumRoleSecurity(list.get().getFamily(), requestingUser, Role.CHILD);

    if (!hasAppropriatePermissions) // If user does not have permissions to add task
    {
      throw new AuthorizationException(
          ApiExceptionCode.USER_PRIVILEGES_TOO_LOW, "User not authorized to complete this action.");
    }

    ToDoTask task = new ToDoTask();
    task.setDescription(request.getDescription());
    task.setNotes(request.getNotes());
    task.setList(list.get());
    task.setCreatedDatetime(Timestamp.from(Instant.now()));
    task.setAddedBy(requestingUser);
    task.setDueDate(
        request.getDueDate() == null ? null : new java.sql.Date(request.getDueDate().getTime()));
    toDoTaskRepository.save(task);
  }

  @Override
  @Transactional
  public void updateTask(ToDoTaskDto request)
      throws UserNotFoundException, AuthorizationException, ResourceNotFoundException {
    if (request.getId() == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Item id must not be null.");
    }
    Optional<ToDoTask> task = toDoTaskRepository.findById(request.getId());

    if (task.isEmpty()) {
      throw new ResourceNotFoundException(
          ApiExceptionCode.LIST_ITEM_DOESNT_EXIST,
          "To do list with id " + request.getId() + " not found.");
    }
    User requestingUser = userService.getRequestingUser();

    boolean hasAppropriatePermissions =
        familyService.verfiyMinimumRoleSecurity(
            task.get().getList().getFamily(), requestingUser, Role.ADULT);
    if (!hasAppropriatePermissions) {
      hasAppropriatePermissions = task.get().getAddedBy().getId().equals(requestingUser.getId());
    }
    if (!hasAppropriatePermissions) {
      throw new AuthorizationException(
          ApiExceptionCode.USER_PRIVILEGES_TOO_LOW,
          "User not authorized to complete this actions.");
    }

    if (request.getDescription() != null) {
      task.get().setDescription(request.getDescription());
    }
    task.get().setNotes(request.getNotes());
    task.get()
        .setDueDate(
            request.getDueDate() == null
                ? null
                : new java.sql.Date(request.getDueDate().getTime()));
    if (request.isCompleted() && request.getCompletedDateTime() == null) {
      task.get().setCompletedDatetime(Timestamp.from(Instant.now()));
    }
    toDoTaskRepository.save(task.get());
  }

  @Override
  @Transactional
  public void deleteTask(Long id)
      throws UserNotFoundException, AuthorizationException, ResourceNotFoundException {
    if (id == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Item id must not be null.");
    }
    Optional<ToDoTask> task = toDoTaskRepository.findById(id);

    if (task.isEmpty()) {
      throw new ResourceNotFoundException(
          ApiExceptionCode.LIST_DOESNT_EXIST, "To do task with id " + id + " not found.");
    }
    User requestingUser = userService.getRequestingUser();

    boolean hasAppropriatePermissions =
        familyService.verfiyMinimumRoleSecurity(
            task.get().getList().getFamily(), requestingUser, Role.ADMIN);
    if (!hasAppropriatePermissions) {
      hasAppropriatePermissions = task.get().getAddedBy().getId().equals(requestingUser.getId());
    }
    if (!hasAppropriatePermissions) {
      throw new AuthorizationException(
          ApiExceptionCode.USER_PRIVILEGES_TOO_LOW, "User not authorized to complete this action.");
    }

    toDoTaskRepository.delete(task.get());
  }

  @Override
  @Transactional
  public ToDoListSearchResponseDto search(ToDoListSearchRequestDto request) {
    User requestingUser = userService.getRequestingUser();

    ToDoListSearchResponseDto response = ToDoListSearchResponseDto.fromRequest(request);
    List<Family> families = familyService.getFamiliesByUser(requestingUser.getUsername());
    // retroactively create a default to do list if one doesn't already exist
    families.forEach(
        family -> {
          if (family.getToDoLists().stream().noneMatch(ToDoList::isDefault)) {
            createDefaultToDoList(family);
          }
        });
    List<Long> permittedFamilyIds =
        families.stream().map(Family::getId).collect(Collectors.toList());
    List<Long> requestFamilyIds = request.getIdsByField(ToDoField.FAMILY);
    List<ToDoList> lists =
        new ArrayList<>(
            toDoListRepository.getFilteredLists(
                requestFamilyIds.isEmpty()
                    ? permittedFamilyIds
                    : permittedFamilyIds.stream()
                        .filter(requestFamilyIds::contains)
                        .collect(Collectors.toList()),
                request.getIdsByField(ToDoField.LIST)));
    Map<Long, List<ToDoTask>> tasksByList =
        toDoTaskRepository.getFilteredTasksByList(
            lists.stream().map(ToDoList::getId).collect(Collectors.toList()),
            request.getStart() == null ? null : new java.sql.Date(request.getStart().getTime()),
            request.getEnd() == null ? null : new java.sql.Date(request.getEnd().getTime()),
            request.getCompleted());

    response.setSearchFilters(getSearchFilters(lists));
    response.setLists(
        lists.stream()
            .map(
                list -> {
                  TimeZone timezone =
                      familyService.getUserTimeZoneOrDefault(requestingUser, list.getFamily());
                  return new ToDoListDtoBuilder()
                      .setId(list.getId())
                      .setDescription(list.getDescription())
                      .setIsDefault(list.isDefault())
                      .setColor(list.getFamily().getEventColor())
                      .setCreatedBy(UserDto.fromUserObj(list.getCreatedBy()))
                      .setCreated(
                          DateUtil.toTimezone(
                              list.getCreatedDatetime(), TimeZone.getDefault(), timezone))
                      .setFamilyId(list.getFamily().getId())
                      .setTasks(
                          tasksByList.getOrDefault(list.getId(), Collections.emptyList()).stream()
                              .map(
                                  item ->
                                      new ToDoTaskDtoBuilder()
                                          .setId(item.getId())
                                          .setDescription(item.getDescription())
                                          .setDueDate(item.getDueDate())
                                          .setAddedBy(UserDto.fromUserObj(item.getAddedBy()))
                                          .setCreated(
                                              DateUtil.toTimezone(
                                                  item.getCreatedDatetime(),
                                                  TimeZone.getDefault(),
                                                  timezone))
                                          .setCompleted(item.getCompletedDatetime() != null)
                                          .setCompletedDateTime(item.getCompletedDatetime())
                                          .setNotes(item.getNotes())
                                          .setListId(item.getList().getId())
                                          .setOverdue(
                                              item.getDueDate() != null
                                                  && DateTimeComparator.getDateOnlyInstance()
                                                          .compare(
                                                              item.getDueDate(), DateTime.now())
                                                      < 0)
                                          .setDueNextTwoDays(
                                              item.getDueDate() != null
                                                  && DateTimeComparator.getDateOnlyInstance()
                                                          .compare(
                                                              item.getDueDate(),
                                                              DateTime.now()
                                                                  .withFieldAdded(
                                                                      DurationFieldType.days(), 2))
                                                      < 0)
                                          .build())
                              .collect(Collectors.toList()))
                      .build();
                })
            .collect(Collectors.toList()));
    return response;
  }

  @Override
  public List<ToDoListDto> getLists(Long familyId) {
    User requestingUser = userService.getRequestingUser();
    List<Long> permittedFamilyIds = familyService.getFamilyIdsByUser(requestingUser.getUsername());
    if (familyId != null && permittedFamilyIds.contains(familyId)) {
      permittedFamilyIds = Collections.singletonList(familyId);
    }
    List<ToDoList> lists =
        toDoListRepository.getFilteredLists(permittedFamilyIds, Collections.emptyList());
    logger.info(lists.toString());
    return lists.stream()
        .map(
            list ->
                new ToDoListDtoBuilder()
                    .setId(list.getId())
                    .setDescription(list.getDescription())
                    .setIsDefault(list.isDefault())
                    .setFamilyId(list.getFamily().getId())
                    .build())
        .collect(Collectors.toList());
  }

  private void createDefaultToDoList(Family family) {
    if (family.getOwner().isPresent()) {
      ToDoList list = new ToDoList();
      list.setDefaultList(true);
      list.setDescription("Family To-Do List");
      list.setFamily(family);
      list.setCreatedDatetime(Timestamp.from(Instant.now()));
      list.setCreatedBy(family.getOwner().get().getUser());
      toDoListRepository.save(list);
    } else {
      throw new IllegalStateException("Family must have an owner.");
    }
  }

  private Map<ToDoField, List<SearchFilter>> getSearchFilters(List<ToDoList> lists) {
    Map<ToDoField, List<SearchFilter>> filters = new HashMap<>();
    filters.put(
        ToDoField.LIST,
        lists.stream()
            .map(list -> new SearchFilter(list.getId(), list.getDescription()))
            .distinct()
            .collect(Collectors.toList()));
    filters.put(
        ToDoField.FAMILY,
        lists.stream()
            .map(list -> new SearchFilter(list.getFamily().getId(), list.getFamily().getName()))
            .distinct()
            .collect(Collectors.toList()));

    return filters;
  }
}
