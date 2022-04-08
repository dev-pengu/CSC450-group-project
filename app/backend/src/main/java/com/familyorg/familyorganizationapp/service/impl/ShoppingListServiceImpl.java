package com.familyorg.familyorganizationapp.service.impl;

import com.familyorg.familyorganizationapp.DTO.ShoppingListDto;
import com.familyorg.familyorganizationapp.DTO.ShoppingListItemDto;
import com.familyorg.familyorganizationapp.DTO.ShoppingListSearchRequestDto;
import com.familyorg.familyorganizationapp.DTO.ShoppingListSearchResponseDto;
import com.familyorg.familyorganizationapp.DTO.UserDto;
import com.familyorg.familyorganizationapp.DTO.builder.ShoppingListDtoBuilder;
import com.familyorg.familyorganizationapp.DTO.builder.ShoppingListItemDtoBuilder;
import com.familyorg.familyorganizationapp.Exception.AuthorizationException;
import com.familyorg.familyorganizationapp.Exception.BadRequestException;
import com.familyorg.familyorganizationapp.Exception.ResourceNotFoundException;
import com.familyorg.familyorganizationapp.domain.Family;
import com.familyorg.familyorganizationapp.domain.Role;
import com.familyorg.familyorganizationapp.domain.ShoppingList;
import com.familyorg.familyorganizationapp.domain.ShoppingListItem;
import com.familyorg.familyorganizationapp.domain.User;
import com.familyorg.familyorganizationapp.domain.search.SearchFilter;
import com.familyorg.familyorganizationapp.domain.search.ShoppingListField;
import com.familyorg.familyorganizationapp.repository.ShoppingListItemRepository;
import com.familyorg.familyorganizationapp.repository.ShoppingListRepository;
import com.familyorg.familyorganizationapp.service.FamilyService;
import com.familyorg.familyorganizationapp.service.ShoppingListService;
import com.familyorg.familyorganizationapp.service.UserService;
import com.familyorg.familyorganizationapp.util.DateUtil;
import io.micrometer.core.instrument.search.Search;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShoppingListServiceImpl implements ShoppingListService {

  ShoppingListRepository listRepository;
  ShoppingListItemRepository itemRepository;
  UserService userService;
  FamilyService familyService;

  @Autowired
  public ShoppingListServiceImpl(
      ShoppingListRepository listRepository,
      ShoppingListItemRepository itemRepository,
      UserService userService,
      FamilyService familyService) {
    this.listRepository = listRepository;
    this.itemRepository = itemRepository;
    this.userService = userService;
    this.familyService = familyService;
  }

  @Override
  @Transactional
  public void createShoppingList(ShoppingListDto request) {
    if (request.getDescription() == null) {
      throw new BadRequestException("List description must not be null.");
    }
    if (request.getFamilyId() == null) {
      throw new BadRequestException("List must have a family id to associate the list with.");
    }
    User requestingUser = userService.getRequestingUser();
    Optional<Family> family = familyService.getFamilyById(request.getFamilyId());
    if (family.isEmpty()) {
      throw new ResourceNotFoundException(
          "Family with id " + request.getFamilyId() + " not found.");
    }
    boolean hasAppropriatePermissions =
        familyService.verfiyMinimumRoleSecurity(family.get(), requestingUser, Role.ADULT);
    if (!hasAppropriatePermissions) {
      throw new AuthorizationException("User not authorized to complete this action.");
    }

    ShoppingList list = new ShoppingList();
    list.setDescription(request.getDescription());
    list.setDefault(false);
    list.setCreatedBy(requestingUser);
    list.setCreatedDatetime(Timestamp.from(Instant.now()));
    list.setFamily(family.get());
    listRepository.save(list);
  }

  @Override
  @Transactional
  public void updateShoppingList(ShoppingListDto request) {
    if (request.getId() == null) {
      throw new BadRequestException("List id must not be null.");
    }
    Optional<ShoppingList> list = listRepository.findById(request.getId());
    if (list.isEmpty()) {
      throw new ResourceNotFoundException("List with id " + request.getId() + " not found.");
    }
    User requestingUser = userService.getRequestingUser();
    boolean hasAppropriatePermissions =
        familyService.verfiyMinimumRoleSecurity(list.get().getFamily(), requestingUser, Role.ADMIN);
    if (!hasAppropriatePermissions) {
      hasAppropriatePermissions = list.get().getCreatedBy().getId().equals(requestingUser.getId());
    }
    if (!hasAppropriatePermissions) {
      throw new AuthorizationException("User not authorized to complete this action.");
    }

    if (request.getDescription() != null) {
      list.get().setDescription(request.getDescription());
    }
    listRepository.save(list.get());
  }

  @Override
  @Transactional
  public void deleteShoppingList(Long id) {
    if (id == null) {
      throw new BadRequestException("Id must not be null.");
    }
    Optional<ShoppingList> list = listRepository.findById(id);
    if (list.isEmpty()) {
      throw new ResourceNotFoundException("List with id " + id + " not found.");
    }
    User requestingUser = userService.getRequestingUser();
    boolean hasAppropriatePermissions =
        familyService.verfiyMinimumRoleSecurity(list.get().getFamily(), requestingUser, Role.ADMIN);
    if (!hasAppropriatePermissions) {
      hasAppropriatePermissions = list.get().getCreatedBy().getId().equals(requestingUser.getId());
    }
    if (!hasAppropriatePermissions) {
      throw new AuthorizationException("User not authorized to complete this action.");
    }
    if (list.get().getDefault()) {
      throw new BadRequestException("The family's default shopping list cannot be deleted.");
    }
    listRepository.delete(list.get());
  }

  @Override
  public ShoppingListDto getShoppingList(Long id) {
    if (id == null) {
      throw new BadRequestException("Id must not be null.");
    }
    Optional<ShoppingList> list = listRepository.findById(id);
    if (list.isEmpty()) {
      throw new ResourceNotFoundException("List with id " + id + " not found.");
    }
    User requestingUser = userService.getRequestingUser();
    boolean hasAppropriatePermissions =
        familyService.verfiyMinimumRoleSecurity(list.get().getFamily(), requestingUser, Role.CHILD);
    if (!hasAppropriatePermissions) {
      throw new AuthorizationException("User not authorized to complete this action.");
    }
    // TODO: update this to use family service timezone getter method
    TimeZone timezone =
        requestingUser.getTimezone() != null
            ? TimeZone.getTimeZone(requestingUser.getTimezone())
            : TimeZone.getTimeZone(list.get().getFamily().getTimezone());
    return new ShoppingListDtoBuilder()
        .withId(list.get().getId())
        .withDescription(list.get().getDescription())
        .withColor(list.get().getFamily().getEventColor())
        .setCreatedBy(UserDto.fromUserObj(list.get().getCreatedBy()))
        .withCreated(
            DateUtil.toTimezone(list.get().getCreatedDatetime(), TimeZone.getDefault(), timezone))
        .withFamilyId(list.get().getFamily().getId())
        .setDefault(list.get().getDefault())
        .setItems(
            list.get().getItems().stream()
                .map(
                    item ->
                        new ShoppingListItemDtoBuilder()
                            .withId(item.getId())
                            .withDescription(item.getDescription())
                            .withAmount(item.getAmount())
                            .withUnits(item.getUnit())
                            .withNotes(item.getNotes())
                            .setAddedBy(item.getAddedBy())
                            .build())
                .collect(Collectors.toList()))
        .build();
  }

  @Override
  @Transactional
  public void addItem(ShoppingListItemDto request) {
    if (request.getDescription() == null) {
      throw new BadRequestException("Item description must not be null.");
    }
    if (request.getListId() == null) {
      throw new BadRequestException("A list id must be specified to create a list item.");
    }
    User requestingUser = userService.getRequestingUser();
    Optional<ShoppingList> list = listRepository.findById(request.getListId());
    if (list.isEmpty()) {
      throw new ResourceNotFoundException("List with id " + request.getListId() + " not found.");
    }
    boolean hasAppropriatePermissions =
        familyService.verfiyMinimumRoleSecurity(list.get().getFamily(), requestingUser, Role.CHILD);
    if (!hasAppropriatePermissions) {
      throw new AuthorizationException("User not authorized to complete this action.");
    }

    ShoppingListItem item = new ShoppingListItem();
    item.setDescription(request.getDescription());
    item.setAmount(request.getAmount());
    item.setUnit(request.getUnit());
    item.setAddedBy(requestingUser);
    item.setNotes(request.getNotes());
    item.setList(list.get());
    itemRepository.save(item);
  }

  @Override
  @Transactional
  public void updateItem(ShoppingListItemDto request) {
    if (request.getId() == null) {
      throw new BadRequestException("Item id must not be null.");
    }
    Optional<ShoppingListItem> item = itemRepository.findById(request.getId());
    if (item.isEmpty()) {
      throw new ResourceNotFoundException("List item with id " + request.getId() + " not found.");
    }
    User requestingUser = userService.getRequestingUser();
    boolean hasAppropriatePermissions =
        familyService.verfiyMinimumRoleSecurity(
            item.get().getList().getFamily(), requestingUser, Role.ADULT);
    if (!hasAppropriatePermissions) {
      hasAppropriatePermissions = item.get().getAddedBy().getId().equals(requestingUser.getId());
    }
    if (!hasAppropriatePermissions) {
      throw new AuthorizationException("User not authorized to complete this action.");
    }

    if (request.getDescription() != null) {
      item.get().setDescription(request.getDescription());
    }
    item.get().setAmount(request.getAmount());
    item.get().setUnit(request.getUnit());
    item.get().setNotes(request.getNotes());

    itemRepository.save(item.get());
  }

  @Override
  @Transactional
  public void deleteItem(Long id) {
    if (id == null) {
      throw new BadRequestException("Item id must not be null.");
    }
    Optional<ShoppingListItem> item = itemRepository.findById(id);
    if (item.isEmpty()) {
      throw new ResourceNotFoundException("List item with id " + id + " not found.");
    }
    User requestingUser = userService.getRequestingUser();
    boolean hasAppropriatePermissions =
        familyService.verfiyMinimumRoleSecurity(
            item.get().getList().getFamily(), requestingUser, Role.ADULT);
    if (!hasAppropriatePermissions) {
      hasAppropriatePermissions = item.get().getAddedBy().getId().equals(requestingUser.getId());
    }
    if (!hasAppropriatePermissions) {
      throw new AuthorizationException("User not authorized to complete this action.");
    }
    itemRepository.delete(item.get());
  }

  @Override
  public ShoppingListItemDto getItem(Long id) {
    if (id == null) {
      throw new BadRequestException("Item id must not be null.");
    }
    Optional<ShoppingListItem> item = itemRepository.findById(id);
    if (item.isEmpty()) {
      throw new ResourceNotFoundException("List item with id " + id + " not found.");
    }
    User requestingUser = userService.getRequestingUser();
    boolean hasAppropriatePermissions =
        familyService.verfiyMinimumRoleSecurity(
            item.get().getList().getFamily(), requestingUser, Role.CHILD);
    if (!hasAppropriatePermissions) {
      throw new AuthorizationException("User not authorized to complete this action.");
    }

    return new ShoppingListItemDtoBuilder()
        .withId(item.get().getId())
        .withDescription(item.get().getDescription())
        .withAmount(item.get().getAmount())
        .withUnits(item.get().getUnit())
        .withNotes(item.get().getNotes())
        .withListId(item.get().getList().getId())
        .setAddedBy(item.get().getAddedBy())
        .build();
  }

  @Override
  public ShoppingListSearchResponseDto search(ShoppingListSearchRequestDto request) {
    User requestingUser = userService.getRequestingUser();

    ShoppingListSearchResponseDto response = new ShoppingListSearchResponseDto();
    response.setActiveSearchFilters(request.getFilters());
    List<ShoppingList> lists = new ArrayList<>();
    List<Family> families = familyService.getFamiliesByUser(requestingUser.getUsername());
    // retroactively create a default shopping list.
    families.forEach(
        family -> {
          if (family.getShoppingLists().stream().filter(ShoppingList::getDefault).count() < 1) {
            createDefaultShoppingList(family, family.getOwner().get().getUser());
          }
        });
    List<Long> permittedFamilyIds =
        families.stream().map(Family::getId).collect(Collectors.toList());
    List<Long> requestFamilyIds = request.getIdsByField(ShoppingListField.FAMILY);
    lists.addAll(
        listRepository.getFilteredPolls(
            requestFamilyIds.isEmpty()
                ? permittedFamilyIds
                : permittedFamilyIds.stream()
                    .filter(id -> requestFamilyIds.contains(id))
                    .collect(Collectors.toList()),
            request.getIdsByField(ShoppingListField.SHOPPING_LIST)));

    // TODO: update this to use family service timezone getter method
    TimeZone timezone = TimeZone.getTimeZone(requestingUser.getTimezone());
    response.setSearchFilters(getSearchFilters(lists));
    response.setLists(
        lists.stream()
            .map(
                list ->
                    new ShoppingListDtoBuilder()
                        .withId(list.getId())
                        .withDescription(list.getDescription())
                        .withColor(list.getFamily().getEventColor())
                        .setCreatedBy(UserDto.fromUserObj(list.getCreatedBy()))
                        .withCreated(
                            DateUtil.toTimezone(
                                list.getCreatedDatetime(), TimeZone.getDefault(), timezone))
                        .withFamilyId(list.getFamily().getId())
                        .setDefault(list.getDefault())
                        .setItems(
                            list.getItems().stream()
                                .map(
                                    item ->
                                        new ShoppingListItemDtoBuilder()
                                            .withId(item.getId())
                                            .withDescription(item.getDescription())
                                            .withAmount(item.getAmount())
                                            .withUnits(item.getUnit())
                                            .withNotes(item.getNotes())
                                            .setAddedBy(item.getAddedBy())
                                            .build())
                                .collect(Collectors.toList()))
                        .build())
            .collect(Collectors.toList()));
    return response;
  }

  private Map<ShoppingListField, List<SearchFilter>> getSearchFilters(List<ShoppingList> lists) {
    Map<ShoppingListField, List<SearchFilter>> filters = new HashMap<>();
    filters.put(
        ShoppingListField.SHOPPING_LIST,
        lists.stream()
            .map(list -> new SearchFilter(list.getId(), list.getDescription()))
            .distinct()
            .collect(Collectors.toList()));
    filters.put(
        ShoppingListField.FAMILY,
        lists.stream()
            .map(list -> new SearchFilter(list.getFamily().getId(), list.getFamily().getName()))
            .distinct()
            .collect(Collectors.toList()));

    return filters;
  }

  private ShoppingList createDefaultShoppingList(Family family, User ownerUser) {
    ShoppingList shoppingList = new ShoppingList();
    shoppingList.setDefault(true);
    shoppingList.setDescription("Family Shopping List");
    shoppingList.setFamily(family);
    shoppingList.setCreatedDatetime(Timestamp.from(Instant.now()));
    shoppingList.setCreatedBy(ownerUser);
    ShoppingList savedShoppingList = listRepository.save(shoppingList);
    return savedShoppingList;
  }
}
