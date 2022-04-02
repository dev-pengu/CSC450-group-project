package com.familyorg.familyorganizationapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import com.familyorg.familyorganizationapp.DTO.FamilyDto;
import com.familyorg.familyorganizationapp.DTO.FamilyMemberDto;
import com.familyorg.familyorganizationapp.DTO.MemberInviteDto;
import com.familyorg.familyorganizationapp.DTO.UserDto;
import com.familyorg.familyorganizationapp.DTO.builder.FamilyDtoBuilder;
import com.familyorg.familyorganizationapp.DTO.builder.FamilyMemberDtoBuilder;
import com.familyorg.familyorganizationapp.DTO.builder.MemberInviteDtoBuilder;
import com.familyorg.familyorganizationapp.DTO.builder.UserDtoBuilder;
import com.familyorg.familyorganizationapp.Exception.AuthorizationException;
import com.familyorg.familyorganizationapp.Exception.BadRequestException;
import com.familyorg.familyorganizationapp.Exception.FamilyNotFoundException;
import com.familyorg.familyorganizationapp.Exception.UserNotFoundException;
import com.familyorg.familyorganizationapp.domain.Family;
import com.familyorg.familyorganizationapp.domain.FamilyMembers;
import com.familyorg.familyorganizationapp.domain.InviteCode;
import com.familyorg.familyorganizationapp.domain.MemberInvite;
import com.familyorg.familyorganizationapp.domain.Role;
import com.familyorg.familyorganizationapp.domain.User;
import com.familyorg.familyorganizationapp.service.FamilyService;
import com.familyorg.familyorganizationapp.service.InviteService;
import com.familyorg.familyorganizationapp.service.MessagingService;
import com.familyorg.familyorganizationapp.service.impl.FamilyServiceImpl;
import com.familyorg.familyorganizationapp.service.impl.InviteServiceImpl;
import com.familyorg.familyorganizationapp.service.impl.MessagingServiceImpl;

public class FamilyControllerTest {

  static FamilyService familyService;
  static FamilyController familyController;
  static InviteService inviteService;
  static MessagingService messagingService;

  @BeforeAll
  public static void setup() throws AddressException, MessagingException {
    familyService = mock(FamilyServiceImpl.class);
    inviteService = mock(InviteServiceImpl.class);
    messagingService = mock(MessagingServiceImpl.class);
    when(messagingService.buildInviteContent(any(String.class), any(String.class)))
        .thenReturn("test");
    doNothing().when(messagingService).sendHtmlEmail(any(String.class), any(String.class),
        any(String.class));
    familyController = new FamilyController(familyService, inviteService, messagingService);
  }

  @Test
  public void getFamily_success() throws Exception {
    /* Given */
    when(familyService.getFamily(any(FamilyDto.class))).thenAnswer(
        invocation -> mockServiceResponse(invocation.getArgument(0), false, false, false, false));
    FamilyDto request = new FamilyDtoBuilder()
        .withRequestingUser(new UserDtoBuilder().withUsername("testuser").build()).withId(1l)
        .build();

    /* When */
    ResponseEntity<?> response = familyController.getFamily(request);

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertTrue(response.getBody() instanceof FamilyDto);
    assertEquals(1l, ((FamilyDto) response.getBody()).getId());
  }

  @Test
  public void getFamilies_success() {
    /* Given */
    when(familyService.getFamiliesByUser(any(Long.class)))
        .thenAnswer(invocation -> mockServiceResponse(invocation.getArgument(0), 2, false));
    Long userId = 1l;

    /* When */
    ResponseEntity<?> response = familyController.getFamilies(userId);

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertTrue(response.getBody() instanceof ArrayList);
    ((List<?>) response.getBody()).forEach(familyDto -> assertTrue(familyDto instanceof FamilyDto));
  }

  @Test
  public void createFamily_success() {
    /* Given */
    when(familyService.createFamily(any(FamilyDto.class))).thenAnswer(
        invocation -> mockServiceResponse(invocation.getArgument(0), false, false, false, false));
    FamilyDto request = new FamilyDtoBuilder().withName("Test Family").withEventColor("fffff")
        .withTimezone("America/Chicago")
        .withOwner(new FamilyMemberDtoBuilder()
            .withUser(new UserDtoBuilder().withUsername("testuser").build())
            .withEventColor("ffffff").build())
        .build();

    /* When */
    ResponseEntity<?> response = familyController.createFamily(request);

    /* Then */
    assertNotNull(response);
    assertEquals(201, response.getStatusCodeValue());
    assertTrue(response.getBody() instanceof FamilyDto);
  }

  @Test
  public void updateFamily_success() {
    /* Given */
    when(familyService.updateFamily(any(FamilyDto.class))).thenAnswer(
        invocation -> mockServiceResponse(invocation.getArgument(0), false, false, false, false));
    FamilyDto request = new FamilyDtoBuilder().withId(1l)
        .withRequestingUser(new UserDtoBuilder().withUsername("testuser").build())
        .withEventColor("000000").build();

    /* When */
    ResponseEntity<?> response = familyController.updateFamily(request);

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertTrue(response.getBody() instanceof FamilyDto);
  }

  @Test
  public void deleteFamily_success() {
    doNothing().when(familyService).deleteFamily(any(Long.class));
    Long familyId = 1l;

    /* When */
    ResponseEntity<?> response = familyController.deleteFamily(familyId);

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_generate_non_persistent_invite_then_200_status_returned() {
    /* Given */
    when(inviteService.createUniqueMemberInvite(any(Long.class), any(String.class)))
        .thenAnswer(invocation -> {
          User user =
              new User(1l, "Test", "User", "testuser", "password", "testemail@test.com", null);
          Family family = new Family(invocation.getArgument(0), "Test", "ffffff", "America/Chicago",
              null, null);
          FamilyMembers owner = new FamilyMembers(user, family, Role.OWNER, "ffffff");
          user.setFamilies(new HashSet<>(Collections.singleton(owner)));
          family.setMembers(new HashSet<>(Collections.singleton(owner)));
          return new MemberInvite(family, invocation.getArgument(1));
        });
    MemberInviteDto request = new MemberInviteDtoBuilder().withFamilyId(1l).withPersistence(false)
        .withRecipientEmail("testemail@test.com").build();

    /* When */
    ResponseEntity<?> response = familyController.generateInvite(request);

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_generate_non_persistent_invite_with_role_then_200_status_returned() {
    /* Given */
    when(inviteService.createUniqueMemberInviteWithRole(any(Long.class), any(String.class),
        any(Role.class))).thenAnswer(invocation -> {
          User user =
              new User(1l, "Test", "User", "testuser", "password", "testemail@test.com", null);
          Family family = new Family(invocation.getArgument(0), "Test", "ffffff", "America/Chicago",
              null, null);
          FamilyMembers owner = new FamilyMembers(user, family, Role.OWNER, "ffffff");
          user.setFamilies(new HashSet<>(Collections.singleton(owner)));
          family.setMembers(new HashSet<>(Collections.singleton(owner)));
          return new MemberInvite(family, invocation.getArgument(1), invocation.getArgument(2));
        });
    MemberInviteDto request = new MemberInviteDtoBuilder().withFamilyId(1l).withPersistence(false)
        .withRecipientEmail("testemail@test.com").withInitialRole(Role.ADULT).build();

    /* When */
    ResponseEntity<?> response = familyController.generateInvite(request);

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_generate_persistent_invite_then_invite_code_populated_on_response() {
    /* Given */
    when(inviteService.generatePersistentMemberInvite(any(Long.class))).thenAnswer(invocation -> {
      Family family =
          new Family(invocation.getArgument(0), "Test", "ffffff", "America/Chicago", null, null);
      User user = new User(1l, "Test", "User", "testuser", "password", "testemail@test.com", null);
      FamilyMembers member = new FamilyMembers(user, family, Role.OWNER, "eaeaea");
      family.addMember(member);
      user.setFamilies(Collections.singleton(member));
      InviteCode invite = new InviteCode(true);
      family.setInviteCode(invite);
      return FamilyDto.fromFamilyObj(family, user);
    });
    MemberInviteDto request =
        new MemberInviteDtoBuilder().withFamilyId(1l).withPersistence(true).build();

    /* When */
    ResponseEntity<?> response = familyController.generateInvite(request);

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertTrue(response.getBody() instanceof FamilyDto);
    assertNotNull(((FamilyDto) response.getBody()).getInviteCode());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void when_get_list_of_invites_then_list_of_invites_returned() {
    /* Given */
    List<MemberInvite> invites = new ArrayList<>();
    Family family = new Family(1l, "Test", "ffffff", "America/Chicago", null, mock(HashSet.class));
    invites.add(new MemberInvite(family, "testemail@test.com"));
    invites.add(new MemberInvite(family, "testemail2@test.com"));
    when(inviteService.getInvites(any(Long.class))).thenReturn(invites);

    /* When */
    ResponseEntity<?> response = familyController.getInvites(family.getId());

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertTrue(response.getBody() instanceof List);
    assertEquals(2, ((List<MemberInvite>) response.getBody()).size());
  }

  @Test
  public void when_join_then_success() {
    /* Given */
    doNothing().when(inviteService).verifyMemberInvite(any(InviteCode.class), any(String.class));
    String inviteCode = new InviteCode(false).getInviteCodeString();
    String color = "ffffff";

    /* When */
    ResponseEntity<?> response = familyController.joinFamily(inviteCode, color);

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void transferOwnership_success() {
    /* Given */
    when(familyService.transferOwnership(any(FamilyDto.class))).thenAnswer(
        invocation -> mockServiceResponse(invocation.getArgument(0), false, false, false, false));
    FamilyDto request = new FamilyDtoBuilder().withId(1l).withOwner(new FamilyMemberDtoBuilder()
        .withUser(new UserDtoBuilder().withUsername("testuser").build()).build()).build();

    /* When */
    ResponseEntity<?> response = familyController.transferOwnership(request);

    /* Then */
    assertNotNull(response);
    assertTrue(response.getBody() instanceof FamilyDto);
    assertEquals(200, response.getStatusCodeValue());
  }

  private FamilyDto mockServiceResponse(FamilyDto request, boolean throwUser, boolean throwFamily,
      boolean throwAuth, boolean throwBadRequest)
      throws UserNotFoundException, FamilyNotFoundException, AuthorizationException {
    if (throwUser) {
      throw new UserNotFoundException();
    }
    if (throwFamily) {
      throw new FamilyNotFoundException();
    }
    if (throwAuth) {
      throw new AuthorizationException();
    }

    if (throwBadRequest) {
      throw new BadRequestException();
    }
    UserDto requestingUser = new UserDtoBuilder().withFirstName("Test").withLastName("User")
        .withUsername("testuser").withId(1l).withEmail("testuser@test.com").build();
    return new FamilyDtoBuilder().withId(1l).withEventColor("ffffff").withInviteCode(null)
        .withName("Test Family").withTimezone("America/Chicago").withRequestingUser(requestingUser)
        .withOwner(new FamilyMemberDto(requestingUser, "ffffff", 1l, Role.OWNER))
        .withMembers(new HashSet<>() {
          {
            add(new FamilyMemberDto(requestingUser, "ffffff", 1l, Role.OWNER));
            add(new FamilyMemberDto(
                new UserDtoBuilder().withId(2l).withEmail("testuser2@test.com")
                    .withFirstName("Test").withLastName("User2").withUsername("testuser2").build(),
                "eaeaea", 1l, Role.ADMIN));
          }
        }).build();
  }

  private List<FamilyDto> mockServiceResponse(Long userId, int numResponse, boolean throwUser)
      throws UserNotFoundException {
    if (throwUser) {
      throw new UserNotFoundException();
    }
    UserDto requestingUser = new UserDtoBuilder().withFirstName("Test").withLastName("User")
        .withUsername("testuser").withId(userId).withEmail("testuser@test.com").build();
    List<FamilyDto> response = new ArrayList<>();
    for (int i = 1; i <= numResponse; i++) {
      response.add(new FamilyDtoBuilder().withId(Long.valueOf(i)).withEventColor("ffffff")
          .withInviteCode(null).withName("Test Family " + i).withTimezone("America/Chicago")
          .withRequestingUser(requestingUser)
          .withOwner(new FamilyMemberDto(requestingUser, "ffffff", Long.valueOf(i), Role.OWNER))
          .withMembers(new HashSet<>(Collections.singleton(
              new FamilyMemberDto(requestingUser, "ffffff", Long.valueOf(i), Role.OWNER))))
          .build());
    }
    return response;
  }
}
