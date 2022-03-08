package com.familyorg.familyorganizationapp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import com.familyorg.familyorganizationapp.DTO.FamilyMemberDto;
import com.familyorg.familyorganizationapp.DTO.UserDto;
import com.familyorg.familyorganizationapp.DTO.builder.FamilyDtoBuilder;
import com.familyorg.familyorganizationapp.DTO.builder.FamilyMemberDtoBuilder;
import com.familyorg.familyorganizationapp.DTO.builder.UserDtoBuilder;
import com.familyorg.familyorganizationapp.Exception.AuthorizationException;
import com.familyorg.familyorganizationapp.Exception.BadRequestException;
import com.familyorg.familyorganizationapp.Exception.FamilyNotFoundException;
import com.familyorg.familyorganizationapp.Exception.UserNotFoundException;
import com.familyorg.familyorganizationapp.service.impl.FamilyServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import com.familyorg.familyorganizationapp.DTO.FamilyDto;
import com.familyorg.familyorganizationapp.domain.Role;
import com.familyorg.familyorganizationapp.service.FamilyService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FamilyControllerTest {

	static FamilyService familyService;
  static FamilyController familyController;

  @BeforeAll
  public static void setup() {
    familyService = mock(FamilyServiceImpl.class);
    familyController = new FamilyController(familyService);
  }

  @Test
  public void getFamily_success() throws Exception {
    /* Given */
    when(familyService.getFamily(any(FamilyDto.class))).thenAnswer(
        invocation -> mockServiceResponse(
            invocation.getArgument(0), false, false, false, false));
    FamilyDto request =
        new FamilyDtoBuilder()
            .withRequestingUser(new UserDtoBuilder().withUsername("testuser").build())
            .withId(1l)
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
  public void when_get_family_and_user_doesnt_exist_then_404_returned() {
    /* Given */
    when(familyService.getFamily(any(FamilyDto.class))).thenAnswer(
        invocation -> mockServiceResponse(
            invocation.getArgument(0), true, false, false, false));
    FamilyDto request =
        new FamilyDtoBuilder()
            .withRequestingUser(new UserDtoBuilder().withUsername("userthatdoesntexist").build())
            .withId(1l)
            .build();

    /* When */
    ResponseEntity<?> response = familyController.getFamily(request);

    /* Then */
    assertNotNull(response);
    assertEquals(404, response.getStatusCodeValue());
  }

  @Test
  public void when_get_family_and_family_doesnt_exist_then_404_returned() {
    /* Given */
    when(familyService.getFamily(any(FamilyDto.class))).thenAnswer(
        invocation -> mockServiceResponse(
            invocation.getArgument(0), false, true, false, false));
    FamilyDto request =
        new FamilyDtoBuilder()
            .withRequestingUser(new UserDtoBuilder().withUsername("testuser").build())
            .withId(4l)
            .build();

    /* When */
    ResponseEntity<?> response = familyController.getFamily(request);

    /* Then */
    assertNotNull(response);
    assertEquals(404, response.getStatusCodeValue());
  }

  @Test
  public void when_get_family_and_user_not_part_of_family_then_401_returned() {
    /* Given */
    when(familyService.getFamily(any(FamilyDto.class))).thenAnswer(
        invocation -> mockServiceResponse(
            invocation.getArgument(0), false, false, true, false));
    FamilyDto request =
        new FamilyDtoBuilder()
            .withRequestingUser(new UserDtoBuilder().withUsername("testuser").build())
            .withId(2l)
            .build();

    /* When */
    ResponseEntity<?> response = familyController.getFamily(request);

    /* Then */
    assertNotNull(response);
    assertEquals(401, response.getStatusCodeValue());
  }

  @Test
  public void getFamilies_success() {
    /* Given */
    when(familyService.getFamiliesByUser(any(Long.class))).thenAnswer(
        invocation -> mockServiceResponse(invocation.getArgument(0), 2, false));
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
  public void when_get_families_and_user_doesnt_exist_then_404_returned() {
    /* Given */
    when(familyService.getFamiliesByUser(any(Long.class))).thenAnswer(
        invocation -> mockServiceResponse(invocation.getArgument(0), 2, true));
    Long userId = 1l;

    /* When */
    ResponseEntity<?> response = familyController.getFamilies(userId);

    /* Then */
    assertNotNull(response);
    assertEquals(404, response.getStatusCodeValue());
  }

  @Test
  public void createFamily_success() {
    /* Given */
    when(familyService.createFamily(any(FamilyDto.class))).thenAnswer(
        invocation -> mockServiceResponse(
            invocation.getArgument(0), false, false, false, false));
    FamilyDto request =
        new FamilyDtoBuilder()
            .withName("Test Family")
            .withEventColor("fffff")
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
  public void when_create_family_and_missing_required_fields_then_400_returned() {
    /* Given */
    when(familyService.createFamily(any(FamilyDto.class))).thenAnswer(
        invocation -> mockServiceResponse(
            invocation.getArgument(0), false, false, false, true));
    FamilyDto request =
        new FamilyDtoBuilder()
            .withEventColor("fffff")
            .withTimezone("America/Chicago")
            .withOwner(new FamilyMemberDtoBuilder()
                .withUser(new UserDtoBuilder().withUsername("testuser").build())
                .withEventColor("ffffff").build())
            .build();

    /* When */
    ResponseEntity<?> response = familyController.createFamily(request);

    /* Then */
    assertNotNull(response);
    assertEquals(400, response.getStatusCodeValue());
  }

  @Test
  public void when_create_family_and_user_doesnt_exist_then_404_returned() {
    /* Given */
    when(familyService.createFamily(any(FamilyDto.class))).thenAnswer(
        invocation -> mockServiceResponse(
            invocation.getArgument(0), true, false, false, false));
    FamilyDto request =
        new FamilyDtoBuilder()
            .withEventColor("fffff")
            .withTimezone("America/Chicago")
            .withName("Test Family")
            .withOwner(new FamilyMemberDtoBuilder()
                .withUser(new UserDtoBuilder().withUsername("testuser").build())
                .withEventColor("ffffff").build())
            .build();

    /* When */
    ResponseEntity<?> response = familyController.createFamily(request);

    /* Then */
    assertNotNull(response);
    assertEquals(404, response.getStatusCodeValue());
  }

  @Test
  public void updateFamily_success() {
    /* Given */
    when(familyService.updateFamily(any(FamilyDto.class))).thenAnswer(
        invocation -> mockServiceResponse(
            invocation.getArgument(0), false, false, false, false));
    FamilyDto request =
        new FamilyDtoBuilder()
            .withId(1l)
            .withRequestingUser(new UserDtoBuilder().withUsername("testuser").build())
            .withEventColor("000000")
            .build();

    /* When */
    ResponseEntity<?> response = familyController.updateFamily(request);

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertTrue(response.getBody() instanceof FamilyDto);
  }

  @Test
  public void when_update_family_and_user_doesnt_exist_then_404_returned() {
    /* Given */
    when(familyService.updateFamily(any(FamilyDto.class))).thenAnswer(
        invocation -> mockServiceResponse(
            invocation.getArgument(0), true, false, false, false));
    FamilyDto request =
        new FamilyDtoBuilder()
            .withId(1l)
            .withRequestingUser(new UserDtoBuilder().withUsername("testuser").build())
            .withEventColor("000000")
            .build();

    /* When */
    ResponseEntity<?> response = familyController.updateFamily(request);

    /* Then */
    assertNotNull(response);
    assertEquals(404, response.getStatusCodeValue());
  }

  @Test
  public void when_update_family_and_family_doesnt_exist_then_404_returned() {
    /* Given */
    when(familyService.updateFamily(any(FamilyDto.class))).thenAnswer(
        invocation -> mockServiceResponse(
            invocation.getArgument(0), false, true, false, false));
    FamilyDto request =
        new FamilyDtoBuilder()
            .withId(1l)
            .withRequestingUser(new UserDtoBuilder().withUsername("testuser").build())
            .withEventColor("000000")
            .build();

    /* When */
    ResponseEntity<?> response = familyController.updateFamily(request);

    /* Then */
    assertNotNull(response);
    assertEquals(404, response.getStatusCodeValue());
  }

  @Test
  public void when_update_family_and_user_not_authorized_then_401_returned() {
    /* Given */
    when(familyService.updateFamily(any(FamilyDto.class))).thenAnswer(
        invocation -> mockServiceResponse(
            invocation.getArgument(0), false, false, true, false));
    FamilyDto request =
        new FamilyDtoBuilder()
            .withId(1l)
            .withRequestingUser(new UserDtoBuilder().withUsername("testuser").build())
            .withEventColor("000000")
            .build();

    /* When */
    ResponseEntity<?> response = familyController.updateFamily(request);

    /* Then */
    assertNotNull(response);
    assertEquals(401, response.getStatusCodeValue());
  }

  @Test
  public void deleteFamily_success() {
    doNothing().when(familyService).deleteFamily(any(Long.class));
    Long familyId = 1l;
    String username = "testuser";

    /* When */
    ResponseEntity<?> response = familyController.deleteFamily(familyId);

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_delete_family_and_user_doesnt_exist_then_404_returned() {
    doThrow(UserNotFoundException.class).when(familyService).deleteFamily(any(Long.class));
    Long familyId = 1l;
    String username = "testuser";

    /* When */
    ResponseEntity<?> response = familyController.deleteFamily(familyId);

    /* Then */
    assertNotNull(response);
    assertEquals(404, response.getStatusCodeValue());
  }

  @Test
  public void when_delete_family_and_family_doesnt_exist_then_404_returned() {
    doThrow(FamilyNotFoundException.class).when(familyService).deleteFamily(any(Long.class));
    Long familyId = 1l;
    String username = "testuser";

    /* When */
    ResponseEntity<?> response = familyController.deleteFamily(familyId);

    /* Then */
    assertNotNull(response);
    assertEquals(404, response.getStatusCodeValue());
  }

  @Test
  public void when_delete_family_and_user_unauthorized_then_401_returned() {
    doThrow(AuthorizationException.class).when(familyService).deleteFamily(any(Long.class));
    Long familyId = 1l;
    String username = "testuser";

    /* When */
    ResponseEntity<?> response = familyController.deleteFamily(familyId);

    /* Then */
    assertNotNull(response);
    assertEquals(401, response.getStatusCodeValue());
  }

  private FamilyDto mockServiceResponse(FamilyDto request, boolean throwUser, boolean throwFamily, boolean throwAuth, boolean throwBadRequest)
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
    UserDto requestingUser =
        new UserDtoBuilder()
            .withFirstName("Test")
            .withLastName("User")
            .withUsername("testuser")
            .withId(1l)
            .withEmail("testuser@test.com")
            .build();
    return new FamilyDtoBuilder()
        .withId(1l)
        .withEventColor("ffffff")
        .withInviteCode(null)
        .withName("Test Family")
        .withTimezone("America/Chicago")
        .withRequestingUser(requestingUser)
        .withOwner(new FamilyMemberDto(requestingUser, "ffffff", 1l, Role.OWNER))
        .withMembers(new HashSet<>(Collections.singleton(new FamilyMemberDto(requestingUser, "ffffff", 1l, Role.OWNER))))
        .build();
  }

  private List<FamilyDto> mockServiceResponse(Long userId, int numResponse, boolean throwUser)
      throws UserNotFoundException {
    if (throwUser) {
      throw new UserNotFoundException();
    }
    UserDto requestingUser =
        new UserDtoBuilder()
            .withFirstName("Test")
            .withLastName("User")
            .withUsername("testuser")
            .withId(userId)
            .withEmail("testuser@test.com")
            .build();
    List<FamilyDto> response = new ArrayList<>();
    for (int i = 1; i <= numResponse; i++) {
      response.add(
          new FamilyDtoBuilder()
              .withId(Long.valueOf(i))
              .withEventColor("ffffff")
              .withInviteCode(null)
              .withName("Test Family " + i)
              .withTimezone("America/Chicago")
              .withRequestingUser(requestingUser)
              .withOwner(new FamilyMemberDto(requestingUser, "ffffff", Long.valueOf(i),Role.OWNER))
              .withMembers(new HashSet<>(Collections.singleton(new FamilyMemberDto(requestingUser, "ffffff", Long.valueOf(i),Role.OWNER))))
              .build());
    }
    return response;
  }
}
