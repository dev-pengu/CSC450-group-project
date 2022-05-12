package com.familyorg.familyorganizationapp.service.impl;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.familyorg.familyorganizationapp.repository.PollOptionRepository;
import com.familyorg.familyorganizationapp.repository.PollRepository;
import com.familyorg.familyorganizationapp.repository.PollVoteRepository;
import com.familyorg.familyorganizationapp.repository.ShoppingListRepository;
import com.familyorg.familyorganizationapp.repository.ToDoListRepository;
import com.familyorg.familyorganizationapp.service.AuthService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import com.familyorg.familyorganizationapp.DTO.FamilyDto;
import com.familyorg.familyorganizationapp.DTO.FamilyMemberDto;
import com.familyorg.familyorganizationapp.DTO.UserDto;
import com.familyorg.familyorganizationapp.DTO.builder.FamilyDtoBuilder;
import com.familyorg.familyorganizationapp.DTO.builder.FamilyMemberDtoBuilder;
import com.familyorg.familyorganizationapp.DTO.builder.UserDtoBuilder;
import com.familyorg.familyorganizationapp.Exception.AuthorizationException;
import com.familyorg.familyorganizationapp.Exception.BadRequestException;
import com.familyorg.familyorganizationapp.Exception.ResourceNotFoundException;
import com.familyorg.familyorganizationapp.Exception.UserNotFoundException;
import com.familyorg.familyorganizationapp.domain.Family;
import com.familyorg.familyorganizationapp.domain.FamilyMembers;
import com.familyorg.familyorganizationapp.domain.Role;
import com.familyorg.familyorganizationapp.domain.User;
import com.familyorg.familyorganizationapp.domain.id.FamilyMemberId;
import com.familyorg.familyorganizationapp.repository.CalendarRepository;
import com.familyorg.familyorganizationapp.repository.FamilyMemberRepository;
import com.familyorg.familyorganizationapp.repository.FamilyRepository;
import com.familyorg.familyorganizationapp.service.UserService;


public class FamilyServiceImplTest {

  private FamilyServiceImpl familyService;

  private FamilyMemberRepository familyMemberRepository;
  private FamilyRepository familyRepository;
  private UserService userService;
  private CalendarRepository calendarRepository;
  private ShoppingListRepository shoppingListRepository;

  static User TEST_USER_1 =
      new User(1l, "Test", "User", "testuser", "password", "testuser@test.com", null);
  static User TEST_USER_2 =
      new User(2l, "Test", "User2", "testuser2", "password", "testuser2@test.com", null);
  static User TEST_USER_3 =
      new User(2l, "Test", "User3", "testuser3", "password", "testuser3@test.com", null);
  static Family FAMILY_1 = new Family(1l, "Test Family 1", "000000", "america/chicago", null, null);
  static Family FAMILY_2 = new Family(2l, "Test Family 2", "ffffff", "america/chicago", null, null);
  static Family FAMILY_3 = new Family(3l, "Test Family 3", "eaeaea", "america/chicago", null, null);
  static List<FamilyMembers> familyOneMembers;
  static List<FamilyMembers> familyTwoMembers;
  static List<FamilyMembers> familyThreeMembers;
  static Map<String, User> usersByEmail = new HashMap<>();
  static Map<String, User> usersByUsername = new HashMap<>();
  static Map<Long, User> usersById = new HashMap<>();
  static Map<Long, Family> familiesById = new HashMap<>();
  static Map<FamilyMemberId, FamilyMembers> familyMembersById = new HashMap<>();

  @BeforeAll
  public static void setup() {
    usersByEmail.put(TEST_USER_1.getEmail(), TEST_USER_1);
    usersByEmail.put(TEST_USER_2.getEmail(), TEST_USER_2);
    usersByEmail.put(TEST_USER_2.getEmail(), TEST_USER_3);
    usersById.put(TEST_USER_1.getId(), TEST_USER_1);
    usersById.put(TEST_USER_2.getId(), TEST_USER_2);
    usersById.put(TEST_USER_2.getId(), TEST_USER_3);
    usersByUsername.put(TEST_USER_1.getUsername(), TEST_USER_1);
    usersByUsername.put(TEST_USER_2.getUsername(), TEST_USER_2);
    usersByUsername.put(TEST_USER_2.getUsername(), TEST_USER_3);

    familiesById.put(FAMILY_1.getId(), FAMILY_1);
    familiesById.put(FAMILY_2.getId(), FAMILY_2);
    familiesById.put(FAMILY_3.getId(), FAMILY_3);
  }

  @BeforeEach
  public void before() {
    familyOneMembers = new ArrayList<>();
    familyOneMembers.add(new FamilyMembers(TEST_USER_1, FAMILY_1, Role.OWNER, "e802d7"));
    FAMILY_1.setMembers(familyOneMembers.stream().collect(Collectors.toSet()));
    familyOneMembers.forEach(familyMember -> {
      familyMembersById.put(
          new FamilyMemberId(familyMember.getUser().getId(), familyMember.getFamily().getId()),
          familyMember);
    });

    familyTwoMembers = new ArrayList<>();
    familyTwoMembers.add(new FamilyMembers(TEST_USER_2, FAMILY_2, Role.OWNER, "000000"));
    FAMILY_2.setMembers(familyTwoMembers.stream().collect(Collectors.toSet()));
    familyTwoMembers.forEach(familyMember -> {
      familyMembersById.put(
          new FamilyMemberId(familyMember.getUser().getId(), familyMember.getFamily().getId()),
          familyMember);
    });

    familyThreeMembers = new ArrayList<>();
    familyThreeMembers.add(new FamilyMembers(TEST_USER_1, FAMILY_3, Role.OWNER, "fffff"));
    familyThreeMembers.add(new FamilyMembers(TEST_USER_2, FAMILY_3, Role.CHILD, "457163"));
    FAMILY_3.setMembers(familyThreeMembers.stream().collect(Collectors.toSet()));
    familyThreeMembers.forEach(familyMember -> {
      familyMembersById.put(
          new FamilyMemberId(familyMember.getUser().getId(), familyMember.getFamily().getId()),
          familyMember);
    });

    TEST_USER_1.setFamilies(
        new HashSet<>(Arrays.asList(familyOneMembers.get(0), familyThreeMembers.get(0))));
    TEST_USER_2.setFamilies(
        new HashSet<>(Arrays.asList(familyTwoMembers.get(0), familyThreeMembers.get(1))));

    userService = mock(UserServiceImpl.class);
    familyRepository = mock(FamilyRepository.class);
    when(familyRepository.save(any(Family.class))).thenAnswer(invocation -> {
      Family family = invocation.getArgument(0);
      if (family.getName() == null) {
        throw new DataIntegrityViolationException("Name cannot be null");
      }
      if (family.getTimezone() == null) {
        throw new DataIntegrityViolationException("Timezone cannot be null");
      }
      if (family.getEventColor() == null) {
        throw new DataIntegrityViolationException("EventColor cannot be null");
      }
      if (family.getId() == null) {
        family.setId(3l);
      }
      return family;
    });
    familyMemberRepository = mock(FamilyMemberRepository.class);
    calendarRepository = mock(CalendarRepository.class);
    shoppingListRepository = mock(ShoppingListRepository.class);
    familyService = new FamilyServiceImpl(familyRepository, familyMemberRepository,
        calendarRepository, userService, shoppingListRepository, mock(AuthService.class), mock(
      PollVoteRepository.class), mock(ToDoListRepository.class), mock(PollRepository.class), mock(
      PollOptionRepository.class));

    when(userService.getUserByEmail(any(String.class)))
        .thenAnswer(invocation -> usersByEmail.get(invocation.getArgument(0)));
    when(userService.getUserById(any(Long.class)))
        .thenAnswer(invocation -> usersById.get(invocation.getArgument(0)));
    when(userService.getUserByUsername(any(String.class)))
        .thenAnswer(invocation -> usersByUsername.get(invocation.getArgument(0)));

    when(familyRepository.findById(any(Long.class))).thenAnswer(invocation -> {
      Family family = familiesById.get(invocation.getArgument(0));
      if (family == null) {
        return Optional.empty();
      }
      return Optional.of(family);
    });
    when(familyRepository.getFamiliesByUserId(any(Long.class)))
        .thenAnswer(invocation -> usersById.get(invocation.getArgument(0))
            .getFamilies()
            .stream()
            .map(familyMember -> familiesById.get(familyMember.getFamily().getId()))
            .collect(Collectors.toList()));
    when(familyMemberRepository.findById(any(FamilyMemberId.class))).thenAnswer(invocation -> {
      FamilyMembers familyMember = familyMembersById.get(invocation.getArgument(0));
      if (familyMember == null) {
        return Optional.empty();
      }
      return Optional.of(familyMember);
    });
    doNothing().when(familyRepository).deleteById(any(Long.class));
  }

  @Test
  public void test_get_family() {
    /* Given */
    when(userService.getRequestingUser()).thenReturn(TEST_USER_1);
    FamilyDto expected = new FamilyDtoBuilder().withId(FAMILY_1.getId())
        .withInviteCode(null)
        .withEventColor(FAMILY_1.getEventColor())
        .withName(FAMILY_1.getName())
        .withTimezone(FAMILY_1.getTimezone())
        .withOwner(FamilyMemberDto.fromFamilyMemberObj(familyOneMembers.get(0)))
        .withMembers(familyOneMembers.stream()
            .map(familyMember -> FamilyMemberDto.fromFamilyMemberObj(familyMember))
            .collect(Collectors.toSet()))
        .build();

    /* When */
    FamilyDto response = familyService.getFamily(1l);

    /* Then */
    assertNotNull(response);
    assertEquals(expected, response);

  }

  @Test
  public void when_get_family_and_not_part_of_family_then_authorization_exception_thrown() {
    /* Given */
    when(userService.getRequestingUser()).thenReturn(TEST_USER_2);

    /* When */
    assertThrows(AuthorizationException.class, () -> {
      familyService.getFamily(1l);
    });
  }

  @Test
  public void test_get_families_by_user_multiple_families() {
    /* Given */
    when(userService.getRequestingUser()).thenReturn(TEST_USER_1);
    List<FamilyDto> expectedFamilyDtos = new ArrayList<>();
    expectedFamilyDtos.add(new FamilyDtoBuilder().withId(FAMILY_1.getId())
        .withInviteCode(null)
        .withEventColor(FAMILY_1.getEventColor())
        .withName(FAMILY_1.getName())
        .withTimezone(FAMILY_1.getTimezone())
        .withOwner(FamilyMemberDto.fromFamilyMemberObj(familyOneMembers.get(0)))
        .withMembers(familyOneMembers.stream()
            .map(familyMember -> FamilyMemberDto.fromFamilyMemberObj(familyMember))
            .collect(Collectors.toSet()))
        .build());
    expectedFamilyDtos.add(new FamilyDtoBuilder().withId(FAMILY_3.getId())
        .withInviteCode(null)
        .withEventColor(FAMILY_3.getEventColor())
        .withName(FAMILY_3.getName())
        .withTimezone(FAMILY_3.getTimezone())
        .withOwner(FamilyMemberDto.fromFamilyMemberObj(familyThreeMembers.get(0)))
        .withMembers(familyThreeMembers.stream()
            .map(familyMember -> FamilyMemberDto.fromFamilyMemberObj(familyMember))
            .collect(Collectors.toSet()))
        .build());

    /* When */
    List<FamilyDto> response = familyService.getFamiliesByUser();

    /* Then */
    assertNotNull(response);
    assertTrue(response.containsAll(expectedFamilyDtos));
  }

  @Test
  public void when_get_families_by_user_and_user_does_not_exist_then_user_not_found_exception_thrown() {
    /* Given */
    doThrow(UserNotFoundException.class).when(userService).getRequestingUser();

    /* When */
    assertThrows(UserNotFoundException.class, () -> {
      familyService.getFamiliesByUser();
    });
  }

  @Test
  public void when_create_with_required_params_then_familydto_returned() {
    /* Given */
    when(userService.getRequestingUser()).thenReturn(TEST_USER_1);
    FamilyDto request = new FamilyDtoBuilder().withEventColor("ffffff")
        .withName("Test Family")
        .withTimezone("America/Chicago")
        .withOwner(new FamilyMemberDtoBuilder().withEventColor("aeaeae").build())
        .build();
    FamilyMemberDto expectedFamilyMemberObj =
        new FamilyMemberDtoBuilder().withUser(UserDto.fromUserObj(TEST_USER_1))
            .withFamilyId(3l)
            .withEventColor("aeaeae")
            .withRole(Role.OWNER)
            .build();
    FamilyDto expected = new FamilyDtoBuilder().withId(3l)
        .withEventColor("ffffff")
        .withName("Test Family")
        .withTimezone("America/Chicago")
        .withMembers(new HashSet<>(Collections.singleton(expectedFamilyMemberObj)))
        .withOwner(expectedFamilyMemberObj)
        .build();

    /* When */
    FamilyDto response = familyService.createFamily(request);

    /* Then */
    assertNotNull(response);
    assertEquals(expected, response);
  }

  @Test
  public void when_create_with_missing_family_params_then_bad_request_exception_thrown() {
    /* Given */
    when(userService.getRequestingUser()).thenReturn(TEST_USER_1);
    FamilyDto request =
        new FamilyDtoBuilder().withEventColor("ffffff")
            .withTimezone("America/Chicago")
            .withOwner(new FamilyMemberDtoBuilder().withEventColor("aeaeae").build())
            .build();

    /* When */
    assertThrows(BadRequestException.class, () -> {
      familyService.createFamily(request);
    });
  }

  @Test
  public void when_update_with_required_params_then_family_updated_and_returned() {
    /* Given */
    when(userService.getRequestingUser()).thenReturn(TEST_USER_1);
    FamilyDto request = new FamilyDtoBuilder().withId(1l).withEventColor("01c89e").build();

    /* When */
    FamilyDto response = familyService.updateFamily(request);

    /* Then */
    assertEquals("01c89e", response.getEventColor());
  }

  @Test
  public void when_update_and_not_part_of_family_then_authorization_exception_thrown() {
    /* Given */
    when(userService.getRequestingUser()).thenReturn(TEST_USER_2);
    FamilyDto request =
        new FamilyDtoBuilder().withId(FAMILY_1.getId()).withEventColor("01c89e").build();

    Map<FamilyMemberId, FamilyMembers> test = familyMembersById;
    /* When */
    assertThrows(AuthorizationException.class, () -> {
      familyService.updateFamily(request);
    });
  }

  @Test
  public void when_update_and_family_id_doesnt_exist_then_family_not_found_exception_thrown() {
    /* Given */
    when(userService.getRequestingUser()).thenReturn(TEST_USER_2);
    FamilyDto request = new FamilyDtoBuilder().withId(4l).withEventColor("01c89e").build();

    /* When */
    assertThrows(ResourceNotFoundException.class, () -> {
      familyService.updateFamily(request);
    });
  }

  @Test
  public void when_update_and_user_doesnt_exist_then_user_not_found_exception_thrown() {
    /* Given */
    doThrow(UserNotFoundException.class).when(userService).getRequestingUser();
    FamilyDto request = new FamilyDtoBuilder().withId(1l).withEventColor("01c89e").build();

    /* When */
    assertThrows(UserNotFoundException.class, () -> {
      familyService.updateFamily(request);
    });
  }

  @Test
  public void when_update_and_user_doesnt_have_gte_admin_then_authorization_exception_thrown() {
    /* Given */
    when(userService.getRequestingUser()).thenReturn(TEST_USER_2);
    FamilyDto request = new FamilyDtoBuilder().withId(3l).withEventColor("01c89e").build();

    /* When */
    assertThrows(AuthorizationException.class, () -> {
      familyService.updateFamily(request);
    });
  }

  @Test
  public void when_delete_and_required_params_included_then_completes_successfully() {
    /* Given */
    Long familyId = 1l;
    when(userService.getRequestingUser()).thenReturn(TEST_USER_1);

    /* When */
    familyService.deleteFamily(familyId);

    /* Then */
    // dont need to assert anything, if completes successfully then working as expected
  }

  @Test
  public void when_delete_and_user_doesnt_exist_then_user_not_found_exception_thrown() {
    /* Given */
    Long familyId = 1l;
    doThrow(UserNotFoundException.class).when(userService).getRequestingUser();

    /* When */
    assertThrows(UserNotFoundException.class, () -> {
      familyService.deleteFamily(familyId);
    });
  }

  @Test
  public void when_delete_and_not_part_of_family_then_authorization_exception_thrown() {
    /* Given */
    Long familyId = 1l;
    when(userService.getRequestingUser()).thenReturn(TEST_USER_2);

    /* When */
    assertThrows(AuthorizationException.class, () -> {
      familyService.deleteFamily(familyId);
    });
  }

  @Test
  public void when_delete_and_not_owner_then_authorization_exception_thrown() {
    /* Given */
    Long familyId = 3l;
    when(userService.getRequestingUser()).thenReturn(TEST_USER_2);

    /* When */
    assertThrows(AuthorizationException.class, () -> {
      familyService.deleteFamily(familyId);
    });
  }

  @Test
  public void when_transfer_ownership_then_memberships_updated() {
    /* Given */
    when(userService.getRequestingUser()).thenReturn(TEST_USER_1);
    FamilyDto request = new FamilyDtoBuilder().withId(FAMILY_3.getId())
        .withOwner(new FamilyMemberDtoBuilder()
            .withUser(new UserDtoBuilder().withUsername(TEST_USER_2.getUsername())
                .withId(TEST_USER_2.getId())
                .build())
            .build())
        .build();

    /* When */
    FamilyDto response = familyService.transferOwnership(request);

    /* Then */
    assertNotNull(response);
    assertEquals(TEST_USER_2.getId(), response.getOwner().getUser().getId());
    assertTrue(response.getMembers()
        .stream()
        .filter(member -> member.getUser().getId().equals(TEST_USER_1.getId())
            && member.getRole().equals(Role.ADMIN))
        .count() > 0);
  }

  @Test
  public void when_transfer_ownership_and_family_doesnt_exist_then_family_not_found_exception_thrown() {
    /* Given */
    when(userService.getRequestingUser()).thenReturn(TEST_USER_1);
    FamilyDto request = new FamilyDtoBuilder().withId(5l)
        .withOwner(new FamilyMemberDtoBuilder()
            .withUser(new UserDtoBuilder().withUsername(TEST_USER_2.getUsername()).build())
            .build())
        .build();

    /* Given */
    assertThrows(ResourceNotFoundException.class, () -> {
      familyService.transferOwnership(request);
    });
  }

  @Test
  public void when_transfer_ownership_and_user_not_in_family_then_authorization_exception_thrown() {
    /* Given */
    when(userService.getRequestingUser()).thenReturn(TEST_USER_1);
    FamilyDto request = new FamilyDtoBuilder().withId(FAMILY_2.getId())
        .withOwner(new FamilyMemberDtoBuilder()
            .withUser(new UserDtoBuilder().withUsername(TEST_USER_1.getUsername()).build())
            .build())
        .build();

    /* Given */
    assertThrows(AuthorizationException.class, () -> {
      familyService.transferOwnership(request);
    });
  }

  @Test
  public void when_transfer_ownership_and_user_is_not_owner_then_authorization_exception_thrown() {
    /* Given */
    when(userService.getRequestingUser()).thenReturn(TEST_USER_2);
    FamilyDto request = new FamilyDtoBuilder().withId(FAMILY_3.getId())
        .withOwner(new FamilyMemberDtoBuilder()
            .withUser(new UserDtoBuilder().withUsername(TEST_USER_2.getUsername()).build())
            .build())
        .build();

    /* Given */
    assertThrows(AuthorizationException.class, () -> {
      familyService.transferOwnership(request);
    });
  }

  @Test
  public void when_transfer_ownership_and_new_owner_doesnt_exist_then_user_not_found_exception_thrown() {
    /* Given */
    when(userService.getRequestingUser()).thenReturn(TEST_USER_1);
    FamilyDto request = new FamilyDtoBuilder().withId(FAMILY_3.getId())
        .withOwner(new FamilyMemberDtoBuilder()
            .withUser(new UserDtoBuilder().withUsername("userthatdoesntexist").build())
            .build())
        .build();

    /* Given */
    assertThrows(UserNotFoundException.class, () -> {
      familyService.transferOwnership(request);
    });
  }

  @Test
  public void when_transfer_ownership_and_new_owner_is_not_part_of_family_then_authorization_exception_thrown() {
    /* Given */
    when(userService.getRequestingUser()).thenReturn(TEST_USER_3);
    FamilyDto request = new FamilyDtoBuilder().withId(FAMILY_1.getId())
        .withOwner(new FamilyMemberDtoBuilder()
            .withUser(new UserDtoBuilder().withUsername(TEST_USER_2.getUsername()).build())
            .build())
        .build();

    /* Given */
    assertThrows(AuthorizationException.class, () -> {
      familyService.transferOwnership(request);
    });
  }
}
