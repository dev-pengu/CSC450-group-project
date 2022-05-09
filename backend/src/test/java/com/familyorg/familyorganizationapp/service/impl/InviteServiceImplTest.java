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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.familyorg.familyorganizationapp.DTO.FamilyDto;
import com.familyorg.familyorganizationapp.Exception.AuthorizationException;
import com.familyorg.familyorganizationapp.Exception.ResourceNotFoundException;
import com.familyorg.familyorganizationapp.domain.Family;
import com.familyorg.familyorganizationapp.domain.FamilyMembers;
import com.familyorg.familyorganizationapp.domain.InviteCode;
import com.familyorg.familyorganizationapp.domain.MemberInvite;
import com.familyorg.familyorganizationapp.domain.Role;
import com.familyorg.familyorganizationapp.domain.User;
import com.familyorg.familyorganizationapp.domain.id.FamilyMemberId;
import com.familyorg.familyorganizationapp.domain.id.MemberInviteId;
import com.familyorg.familyorganizationapp.repository.MemberInviteRepository;
import com.familyorg.familyorganizationapp.service.AuthService;
import com.familyorg.familyorganizationapp.service.FamilyService;
import com.familyorg.familyorganizationapp.service.UserService;

public class InviteServiceImplTest {
  private InviteServiceImpl inviteService;

  private MemberInviteRepository memberInviteRepository;
  private FamilyService familyService;
  private AuthService authService;
  private UserService userService;

  static User TEST_USER_1 =
      new User(1l, "Test", "User", "testuser", "password", "testuser@test.com", null);
  static User TEST_USER_2 =
      new User(2l, "Test", "User2", "testuser2", "password", "testuser2@test.com", null);
  static User TEST_USER_3 =
      new User(3l, "Test", "User3", "testuser3", "password", "testuser3@test.com", null);
  static User TEST_USER_4 =
      new User(4l, "Test", "User4", "testuser4", "password", "testuser4@test.com", null);
  static Family FAMILY_1 = new Family(1l, "Test Family 1", "#000000", "america/chicago",
      "68e7fdfb-8f44-4ca1-96d9-d9c462bb19ba", null);
  static MemberInvite INVITE_1 = new MemberInvite(FAMILY_1, TEST_USER_2.getEmail());
  static MemberInvite INVITE_2 = new MemberInvite(FAMILY_1, "usernotindb@test.com", Role.ADULT);
  static MemberInvite INVITE_3 =
      new MemberInvite(new Family(2l, "Test", "ff0000", "america/chicago", null, null), TEST_USER_2.getEmail());
  static List<FamilyMembers> familyOneMembers;
  static Map<String, User> usersByEmail = new HashMap<>();
  static Map<String, User> usersByUsername = new HashMap<>();
  static Map<Long, User> usersById = new HashMap<>();
  static Map<FamilyMemberId, FamilyMembers> familyMembersById = new HashMap<>();
  static Map<MemberInviteId, MemberInvite> invitesById = new HashMap<>();
  static Map<String, MemberInvite> invitesByCode = new HashMap<>();
  static Map<Long, Family> familiesById = new HashMap<>();
  static Map<String, Family> familiesByInviteCode = new HashMap<>();

  @BeforeAll
  public static void setup() {
    familyOneMembers = new ArrayList<>();
    familyOneMembers.add(new FamilyMembers(TEST_USER_1, FAMILY_1, Role.OWNER, "e802d7"));
    familyOneMembers.add(new FamilyMembers(TEST_USER_3, FAMILY_1, Role.CHILD, "ffffff"));
    FAMILY_1.setMembers(familyOneMembers.stream().collect(Collectors.toSet()));
    familyOneMembers.forEach(familyMember -> {
      familyMembersById.put(
          new FamilyMemberId(familyMember.getUser().getId(), familyMember.getFamily().getId()),
          familyMember);
    });

    TEST_USER_1.setFamilies(Collections.singleton(familyOneMembers.get(0)));
    TEST_USER_3.setFamilies(Collections.singleton(familyOneMembers.get(1)));
    usersByEmail.put(TEST_USER_1.getEmail(), TEST_USER_1);
    usersByEmail.put(TEST_USER_2.getEmail(), TEST_USER_2);
    usersByEmail.put(TEST_USER_3.getEmail(), TEST_USER_3);
    usersByEmail.put(TEST_USER_4.getEmail(), TEST_USER_4);
    usersById.put(TEST_USER_1.getId(), TEST_USER_1);
    usersById.put(TEST_USER_2.getId(), TEST_USER_2);
    usersById.put(TEST_USER_3.getId(), TEST_USER_3);
    usersById.put(TEST_USER_4.getId(), TEST_USER_4);
    usersByUsername.put(TEST_USER_1.getUsername(), TEST_USER_1);
    usersByUsername.put(TEST_USER_2.getUsername(), TEST_USER_2);
    usersByUsername.put(TEST_USER_3.getUsername(), TEST_USER_3);
    usersByUsername.put(TEST_USER_4.getUsername(), TEST_USER_4);
    invitesById.put(new MemberInviteId(INVITE_1.getFamilyId(), INVITE_1.getUserEmail(),
        INVITE_1.getInviteCode()), INVITE_1);
    invitesById.put(new MemberInviteId(INVITE_2.getFamilyId(), INVITE_2.getUserEmail(),
        INVITE_2.getInviteCode()), INVITE_2);
    invitesById.put(new MemberInviteId(INVITE_3.getFamilyId(), INVITE_3.getUserEmail(),
        INVITE_3.getInviteCode()), INVITE_3);
    invitesByCode.put(INVITE_1.getInviteCode(), INVITE_1);
    invitesByCode.put(INVITE_2.getInviteCode(), INVITE_2);
    invitesByCode.put(INVITE_3.getInviteCode(), INVITE_3);
    familiesById.put(FAMILY_1.getId(), FAMILY_1);
    familiesByInviteCode.put(FAMILY_1.getInviteCode(), FAMILY_1);

  }

  @BeforeEach
  public void before() {
    userService = mock(UserServiceImpl.class);
    authService = mock(AuthServiceImpl.class);
    familyService = mock(FamilyServiceImpl.class);
    memberInviteRepository = mock(MemberInviteRepository.class);
    when(userService.getUserByEmail(any(String.class)))
        .thenAnswer(invocation -> usersByEmail.get(invocation.getArgument(0)));
    when(userService.getUserById(any(Long.class)))
        .thenAnswer(invocation -> usersById.get(invocation.getArgument(0)));
    when(userService.getUserByUsername(any(String.class)))
        .thenAnswer(invocation -> usersByUsername.get(invocation.getArgument(0)));
    when(memberInviteRepository.findById(any(MemberInviteId.class)))
        .thenAnswer(invocation -> invitesById.get(invocation.getArgument(0)));
    when(memberInviteRepository.findByInviteCode(any(String.class)))
        .thenAnswer(invocation -> invitesByCode.get(invocation.getArgument(0)));
    when(familyService.getFamilyById(any(Long.class))).thenAnswer(invocation -> {
      Family family = familiesById.get(invocation.getArgument(0));
      if (family == null) {
        return Optional.empty();
      }
      return Optional.of(family);
    });
    when(familyService.getFamilyByInviteCode(any(String.class)))
        .thenAnswer(invocation -> familiesByInviteCode.get(invocation.getArgument(0)));
    when(memberInviteRepository.save(any(MemberInvite.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(familyService.updateFamily(any(Family.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    doNothing().when(memberInviteRepository).delete(any(MemberInvite.class));;

    inviteService =
        new InviteServiceImpl(memberInviteRepository, familyService, authService, userService);
  }

  @Test
  public void when_permitted_and_create_one_time_code_then_code_generated() {
    /* Given */
    when(userService.getRequestingUser()).thenReturn(TEST_USER_1);
    when(familyService.verfiyMinimumRoleSecurity(any(Family.class), any(User.class),
        any(Role.class)))
            .thenReturn(Boolean.TRUE);

    /* When */
    MemberInvite generated =
        inviteService.createUniqueMemberInvite(FAMILY_1.getId(), "testemail@test.com");

    /* Then */
    assertNotNull(generated);
    assertNotNull(generated.getInviteCode());
    assertTrue(generated.getInviteCodeObj()
        .getInviteCodeString()
        .contains(InviteCode.ONE_TIME_USE_PREFIX));
    assertEquals(Role.CHILD, generated.getRole());
  }

  @Test
  public void when_permitted_and_create_one_time_with_role_then_code_generated() {
    /* Given */
    when(userService.getRequestingUser()).thenReturn(TEST_USER_1);
    when(familyService.verfiyMinimumRoleSecurity(any(Family.class), any(User.class),
        any(Role.class)))
            .thenReturn(Boolean.TRUE);
    /* When */
    MemberInvite generated = inviteService.createUniqueMemberInviteWithRole(FAMILY_1.getId(),
        "testuser@test.com", Role.ADULT);

    /* Then */
    assertNotNull(generated);
    assertNotNull(generated.getInviteCode());
    assertTrue(generated.getInviteCodeObj()
        .getInviteCodeString()
        .contains(InviteCode.ONE_TIME_USE_PREFIX));
    assertEquals(Role.ADULT, generated.getRole());
  }

  @Test
  public void when_not_member_of_family_and_try_to_generate_code_then_authorization_exception_thrown() {
    /* Given */
    when(userService.getRequestingUser()).thenReturn(TEST_USER_2);

    /* When */
    assertThrows(AuthorizationException.class, () -> {
      inviteService.createUniqueMemberInvite(FAMILY_1.getId(), "testuser@test.com");
    });
  }

  @Test
  public void when_requesting_user_lt_adult_level_and_try_to_generate_code_then_authorization_exception_thrown() {
    /* Given */
    when(userService.getRequestingUser()).thenReturn(TEST_USER_3);

    /* When */
    assertThrows(AuthorizationException.class, () -> {
      inviteService.createUniqueMemberInvite(FAMILY_1.getId(), "testuser@test.com");
    });
  }

  @Test
  public void when_family_doesnt_exist_and_try_to_generate_code_then_familynotfound_exception_thrown() {
    /* Given */
    when(userService.getRequestingUser()).thenReturn(TEST_USER_1);

    /* When */
    assertThrows(ResourceNotFoundException.class, () -> {
      inviteService.createUniqueMemberInvite(2l, "testuser@test.com");
    });
  }

  @Test
  public void when_permitted_and_try_to_generate_permanent_code_then_code_generated() {
    /* Given */
    when(userService.getRequestingUser()).thenReturn(TEST_USER_1);
    when(familyService.verfiyMinimumRoleSecurity(any(Family.class), any(User.class),
        any(Role.class)))
            .thenReturn(Boolean.TRUE);

    /* When */
    FamilyDto response = inviteService.generatePersistentMemberInvite(FAMILY_1.getId());

    /* Then */
    assertNotNull(response);
    assertNotNull(response.getInviteCode());
    assertTrue(response.getInviteCode().contains(InviteCode.PERSISTENT_PREFIX));
  }

  @Test
  public void when_not_part_of_family_and_try_to_generate_permanent_invite_code_then_authorizationexception_thrown() {
    /* Given */
    when(userService.getRequestingUser()).thenReturn(TEST_USER_2);

    /* When */
    assertThrows(AuthorizationException.class, () -> {
      inviteService.generatePersistentMemberInvite(FAMILY_1.getId());
    });
  }

  @Test
  public void when_not_admin_and_try_to_generate_permanent_invite_code_then_authorizationexception_thrown() {
    /* Given */
    when(userService.getRequestingUser()).thenReturn(TEST_USER_3);

    /* When */
    assertThrows(AuthorizationException.class, () -> {
      inviteService.generatePersistentMemberInvite(FAMILY_1.getId());
    });
  }

  @Test
  public void when_family_doesnt_exist_and_try_to_generate_permanent_code_then_familynotfoundexception_thrown() {
    /* Given */
    when(userService.getRequestingUser()).thenReturn(TEST_USER_1);

    /* When */
    assertThrows(ResourceNotFoundException.class, () -> {
      inviteService.generatePersistentMemberInvite(3l);
    });
  }

  @Test
  public void when_join_family_with_persistent_code_then_no_errors_thrown() {
    /* Given */
    FAMILY_1.setInviteCode("68e7fdfb-8f44-4ca1-96d9-d9c462bb19ba");
    InviteCode code = FAMILY_1.getInviteCodeObj();
    String eventColor = "ffffff";
    when(userService.getRequestingUser()).thenReturn(TEST_USER_4);

    /* When */
    inviteService.verifyMemberInvite(code, eventColor);

    /* Then */
    Family family = FAMILY_1;
    assertTrue(family.getMembers()
        .stream()
        .filter(member -> member.getUser().getId().equals(TEST_USER_4.getId()))
        .count() > 0);
  }

  @Test
  public void when_join_family_and_user_not_logged_in_then_authorizationexception_thrown() {
    /* Given */
    InviteCode code = FAMILY_1.getInviteCodeObj();
    String eventColor = "ffffff";
    doThrow(AuthorizationException.class).when(userService).getRequestingUser();

    /* When */
    assertThrows(AuthorizationException.class, () -> {
      inviteService.verifyMemberInvite(code, eventColor);
    });
  }

  @Test
  public void when_join_family_with_persistent_code_and_family_dne_then_invitecodenotfoundexception_thrown() {
    /* Given */
    InviteCode code = new InviteCode(true);
    if (code.getCode().equals(FAMILY_1.getInviteCodeObj().getCode())) {
      code = new InviteCode(true);
    }
    String eventColor = "ffffff";
    when(userService.getRequestingUser()).thenReturn(TEST_USER_4);
    InviteCode finalCode = code;

    /* When */
    assertThrows(ResourceNotFoundException.class, () -> {
      inviteService.verifyMemberInvite(finalCode, eventColor);
    });
  }

  @Test
  public void when_join_family_with_otu_code_then_no_errors_thrown() {
    /* Given */
    InviteCode code = INVITE_1.getInviteCodeObj();
    String eventColor = "ffffff";
    when(userService.getRequestingUser()).thenReturn(TEST_USER_2);

    /* When */
    inviteService.verifyMemberInvite(code, eventColor);

    /* Then */
    Family family = FAMILY_1;
    assertTrue(family.getMembers()
        .stream()
        .filter(member -> member.getUser().getId().equals(TEST_USER_2.getId()))
        .count() > 0);
  }

  @Test
  public void when_join_family_with_otu_code_and_invite_doesnt_exist_then_invitenotfoundexception_thrown() {
    /* Given */
    InviteCode code = new InviteCode(false);
    if (code.getCode().equals(INVITE_1.getInviteCodeObj().getCode())
        || code.getCode().equals(INVITE_2.getInviteCodeObj().getCode())) {
      code = new InviteCode(false);
    }
    String eventColor = "ffffff";
    when(userService.getRequestingUser()).thenReturn(TEST_USER_2);

    InviteCode finalCode = code;
    /* When */
    assertThrows(ResourceNotFoundException.class, () -> {
      inviteService.verifyMemberInvite(finalCode, eventColor);
    });
  }

  @Test
  public void when_join_family_with_otu_code_and_not_user_on_invite_then_authorizationexception_thrown() {
    /* Given */
    InviteCode code = INVITE_1.getInviteCodeObj();
    String eventColor = "ffffff";
    when(userService.getRequestingUser()).thenReturn(TEST_USER_4);

    /* When */
    assertThrows(AuthorizationException.class, () -> {
      inviteService.verifyMemberInvite(code, eventColor);
    });
  }

  @Test
  public void when_join_family_with_otu_code_and_family_from_invite_no_longer_exists_then_familynotfound_exception_thrown() {
    /* Given */
    InviteCode code = INVITE_3.getInviteCodeObj();
    String eventColor = "ffffff";
    when(userService.getRequestingUser()).thenReturn(TEST_USER_2);

    /* When */
    assertThrows(ResourceNotFoundException.class, () -> {
      inviteService.verifyMemberInvite(code, eventColor);
    });
  }

}
