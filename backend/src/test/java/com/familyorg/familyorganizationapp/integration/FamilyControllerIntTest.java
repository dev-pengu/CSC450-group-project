package com.familyorg.familyorganizationapp.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.familyorg.familyorganizationapp.DTO.FamilyDto;
import com.familyorg.familyorganizationapp.DTO.FamilyMemberDto;
import com.familyorg.familyorganizationapp.DTO.FamilyRoleUpdateRequest;
import com.familyorg.familyorganizationapp.DTO.MemberInviteDto;
import com.familyorg.familyorganizationapp.DTO.builder.FamilyDtoBuilder;
import com.familyorg.familyorganizationapp.DTO.builder.FamilyMemberDtoBuilder;
import com.familyorg.familyorganizationapp.DTO.builder.MemberInviteDtoBuilder;
import com.familyorg.familyorganizationapp.DTO.builder.UserDtoBuilder;
import com.familyorg.familyorganizationapp.Exception.ApiExceptionCode;
import com.familyorg.familyorganizationapp.FamilyOrganizationAppApplication;
import com.familyorg.familyorganizationapp.domain.Family;
import com.familyorg.familyorganizationapp.domain.Role;
import com.familyorg.familyorganizationapp.domain.User;
import com.familyorg.familyorganizationapp.service.MessagingService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    classes = FamilyOrganizationAppApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class FamilyControllerIntTest {

  @LocalServerPort private int port;
  @Autowired private MessagingService messagingServiceImpl;

  @Value("${integration.user.password}")
  private String testPassword;

  @Value("${integration.owner.username}")
  private String ownerUsername;

  @Value("${integration.email.domain}")
  private String testDomain;

  @Value("${integration.admin.username}")
  private String adminUsername;

  @Value("${integration.adult.username}")
  private String adultUsername;

  TestRestTemplate restTemplate = new TestRestTemplate();
  HttpHeaders headers = new HttpHeaders();

  @Before
  public void setup() {
    restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    restTemplate
        .getRestTemplate()
        .setErrorHandler(
            new DefaultResponseErrorHandler() {
              public boolean hasError(ClientHttpResponse response) throws IOException {
                HttpStatus statusCode = response.getStatusCode();
                return statusCode.series() == HttpStatus.Series.SERVER_ERROR;
              }
            });
  }

  @Test
  public void createFamily_success() {
    /* Given */
    FamilyDto family =
        new FamilyDtoBuilder()
            .withName("Test Family 2")
            .withEventColor("0000ff")
            .withTimezone("US/Central")
            .withOwner(new FamilyMemberDtoBuilder().withEventColor("00ff00").build())
            .build();
    HttpEntity<FamilyDto> entity = new HttpEntity<>(family, headers);
    loginUser(adminUsername);

    /* When */
    ResponseEntity<LinkedHashMap> responseEntity =
        restTemplate.exchange(createUrlWithPort(""), HttpMethod.POST, entity, LinkedHashMap.class);

    /* Then */
    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertEquals(family.getName(), responseEntity.getBody().get("name"));
  }

  @Test
  public void getFamily_success() {
    /* Given */
    Map<String, Object> params = new HashMap<>();
    Long id = 1L;
    params.put("id", id);
    HttpEntity entity = new HttpEntity(null, headers);
    loginUser(ownerUsername);

    /* When */
    ResponseEntity<LinkedHashMap> responseEntity =
        restTemplate.exchange(
            createUrlWithPort("?id={id}"), HttpMethod.GET, entity, LinkedHashMap.class, params);

    /* Then */
    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(id, Integer.toUnsignedLong((Integer) responseEntity.getBody().get("id")));
    LinkedHashMap ownerObj = (LinkedHashMap) responseEntity.getBody().get("owner");
    assertEquals(
        1L, Integer.toUnsignedLong((Integer) ((LinkedHashMap) ownerObj.get("user")).get("id")));
  }

  @Test
  public void getFamilies_success() {
    /* Given */
    HttpEntity entity = new HttpEntity(null, headers);
    loginUser(ownerUsername);

    /* When */
    ResponseEntity<Object> responseEntity =
        restTemplate.exchange(
            createUrlWithPort("/get-family"), HttpMethod.GET, entity, Object.class);

    /* Then */
    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    List<LinkedHashMap> responseFamilies = (ArrayList<LinkedHashMap>) responseEntity.getBody();
    assertTrue(responseFamilies.size() > 0);
  }

  @Test
  public void leaveFamily_success() {
    /* Given */
    Long id = 1L;
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    HttpEntity entity = new HttpEntity(null, headers);
    loginUser(adultUsername);

    /* When */
    ResponseEntity<String> responseEntity =
        restTemplate.exchange(
            createUrlWithPort("/leave?id={id}"), HttpMethod.DELETE, entity, String.class, params);

    /* Then */
    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    // Try to access the family the user just left, we should get a 401
    ResponseEntity<LinkedHashMap> familyResponse =
        restTemplate.exchange(
            createUrlWithPort("?id={id}"), HttpMethod.GET, entity, LinkedHashMap.class, params);
    assertNotNull(familyResponse);
    assertNotNull(familyResponse.getBody());
    assertEquals(HttpStatus.UNAUTHORIZED, familyResponse.getStatusCode());
    assertEquals(
        ApiExceptionCode.USER_NOT_IN_FAMILY.getCode(), familyResponse.getBody().get("errorCode"));
  }

  @Test
  public void getFamiliesForSelect_success() {
    /* Given */
    HttpEntity entity = new HttpEntity(null, headers);
    loginUser(ownerUsername);

    /* When */
    ResponseEntity<Object> responseEntity =
        restTemplate.exchange(
            createUrlWithPort("/familySelect"), HttpMethod.GET, entity, Object.class);

    /* Then */
    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    List<LinkedHashMap> responseFamilies = (ArrayList<LinkedHashMap>) responseEntity.getBody();
    assertTrue(responseFamilies.size() > 0);
  }

  @Test
  public void getMembersForSelect_success() {
    /* Given */
    Long id = 1L;
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    HttpEntity entity = new HttpEntity(null, headers);
    loginUser(ownerUsername);

    /* When */
    ResponseEntity<Object> responseEntity =
        restTemplate.exchange(
            createUrlWithPort("/memberSelect?id={id}"),
            HttpMethod.GET,
            entity,
            Object.class,
            params);

    /* Then */
    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    List<LinkedHashMap> responseMembers = (ArrayList<LinkedHashMap>) responseEntity.getBody();
    // depending on if this test runs before or after the leave family test and the remove member
    // test, this family should have either 2, 3 or 4 members in it
    assertTrue(responseMembers.size() >= 2);
  }

  @Test
  public void joinFamily_with_persistent_invite_success() {
    /* Given */
    Map<String, Object> params = new HashMap<>();
    params.put("code", "PRS-5794be52-c089-444b-b84c-7691299986b0");
    params.put("eventColor", "ff0000");
    HttpEntity entity = new HttpEntity(null, headers);
    loginUser("testuser5");

    /* When */
    ResponseEntity<String> responseEntity =
        restTemplate.exchange(
            createUrlWithPort("/invite/join?code={code}&eventColor={eventColor}"),
            HttpMethod.GET,
            entity,
            String.class,
            params);

    /* Then */
    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    // We should now be able to access this family
    Map<String, Object> familyParams = new HashMap<>();
    familyParams.put("id", 1L);
    ResponseEntity<LinkedHashMap> familyResponse =
        restTemplate.exchange(
            createUrlWithPort("?id={id}"),
            HttpMethod.GET,
            entity,
            LinkedHashMap.class,
            familyParams);
    assertNotNull(familyResponse);
    assertNotNull(familyResponse.getBody());
    assertEquals(HttpStatus.OK, familyResponse.getStatusCode());
  }

  @Test
  public void joinFamily_with_user_specific_invite_success() {
    /* Given */
    Map<String, Object> params = new HashMap<>();
    params.put("code", "OTU-d8bb4a14-4bca-4258-b2f3-c124368e11fb");
    params.put("eventColor", "ff0000");
    HttpEntity entity = new HttpEntity(null, headers);
    loginUser("testuser6");

    /* When */
    ResponseEntity<String> responseEntity =
        restTemplate.exchange(
            createUrlWithPort("/invite/join?code={code}&eventColor={eventColor}"),
            HttpMethod.GET,
            entity,
            String.class,
            params);

    /* Then */
    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    // We should now be able to access this family
    Map<String, Object> familyParams = new HashMap<>();
    familyParams.put("id", 1L);
    ResponseEntity<LinkedHashMap> familyResponse =
        restTemplate.exchange(
            createUrlWithPort("?id={id}"),
            HttpMethod.GET,
            entity,
            LinkedHashMap.class,
            familyParams);
    assertNotNull(familyResponse);
    assertNotNull(familyResponse.getBody());
    assertEquals(HttpStatus.OK, familyResponse.getStatusCode());
  }

  @Test
  public void updateFamily_success() {
    /* Given */
    FamilyDto family = new FamilyDtoBuilder().withId(2L).withName("Updated Family").build();
    HttpEntity<FamilyDto> entity = new HttpEntity<>(family, headers);
    loginUser(ownerUsername);

    /* When */
    ResponseEntity<LinkedHashMap> responseEntity =
        restTemplate.exchange(
            createUrlWithPort("/admin/update"), HttpMethod.PATCH, entity, LinkedHashMap.class);

    /* Then */
    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals("Updated Family", responseEntity.getBody().get("name"));
  }

  @Test
  public void removeMember_success() {
    /* Given */
    Map<String, Object> params = new HashMap<>();
    params.put("familyId", 2L);
    params.put("userId", 2L);
    HttpEntity entity = new HttpEntity(null, headers);
    loginUser(ownerUsername);

    /* When */
    ResponseEntity<String> responseEntity =
        restTemplate.exchange(
            createUrlWithPort("/admin/removeMember?familyId={familyId}&userId={userId}"),
            HttpMethod.DELETE,
            entity,
            String.class,
            params);

    /* Then */
    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    // login as the kicked member and see if they have access. we should get a 401
    loginUser("testchild");
    Map<String, Object> familyParams = new HashMap<>();
    familyParams.put("id", 2L);
    ResponseEntity<LinkedHashMap> familyResponse =
        restTemplate.exchange(
            createUrlWithPort("?id={id}"),
            HttpMethod.GET,
            entity,
            LinkedHashMap.class,
            familyParams);
    assertNotNull(familyResponse);
    assertNotNull(familyResponse.getBody());
    assertEquals(HttpStatus.UNAUTHORIZED, familyResponse.getStatusCode());
    assertEquals(
        ApiExceptionCode.USER_NOT_IN_FAMILY.getCode(), familyResponse.getBody().get("errorCode"));
  }

  @Test
  public void removeMembers_success() {
    /* Given */
    HttpEntity entity = new HttpEntity(null, headers);
    loginUser(ownerUsername);
    UriComponents comp =
        UriComponentsBuilder.fromHttpUrl(
                "http://localhost:" + port + "/api/v1/family/admin/removeMembers")
            .queryParam("familyId", 2L)
            .queryParam("userIds", Arrays.asList(3L, 5L))
            .build();

    /* When */
    ResponseEntity<String> responseEntity =
        restTemplate.exchange(comp.expand(12).toString(), HttpMethod.DELETE, entity, String.class);

    /* Then */
    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    // login as the kicked member and see if they have access. we should get a 401
    loginUser("testuser5");
    Map<String, Object> familyParams = new HashMap<>();
    familyParams.put("id", 2L);
    ResponseEntity<LinkedHashMap> familyResponse =
        restTemplate.exchange(
            createUrlWithPort("?id={id}"),
            HttpMethod.GET,
            entity,
            LinkedHashMap.class,
            familyParams);
    assertNotNull(familyResponse);
    assertNotNull(familyResponse.getBody());
    assertEquals(HttpStatus.UNAUTHORIZED, familyResponse.getStatusCode());
    assertEquals(
        ApiExceptionCode.USER_NOT_IN_FAMILY.getCode(), familyResponse.getBody().get("errorCode"));
  }

  @Test
  public void deleteFamily_success() {
    /* Given */
    Long id = 4L;
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    HttpEntity entity = new HttpEntity(null, headers);
    loginUser(ownerUsername);

    /* When */
    ResponseEntity<String> responseEntity =
        restTemplate.exchange(
            createUrlWithPort("/admin/delete?id={id}"),
            HttpMethod.DELETE,
            entity,
            String.class,
            params);

    /* Then */
    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    // The family should no longer exist. if we try to receive it, we should get a 404
    ResponseEntity<LinkedHashMap> familyResponse =
        restTemplate.exchange(
            createUrlWithPort("?id={id}"), HttpMethod.GET, entity, LinkedHashMap.class, params);
    assertNotNull(familyResponse);
    assertNotNull(familyResponse.getBody());
    assertEquals(HttpStatus.NOT_FOUND, familyResponse.getStatusCode());
    assertEquals(
        ApiExceptionCode.FAMILY_DOESNT_EXIST.getCode(), familyResponse.getBody().get("errorCode"));
  }

  @Test
  public void transferOwnership_success() {
    /* Given */
    FamilyDto family =
        new FamilyDtoBuilder()
            .withId(3L)
            .withOwner(
                new FamilyMemberDtoBuilder()
                    .withUser(new UserDtoBuilder().withId(4L).withUsername("testadult").build())
                    .build())
            .build();
    HttpEntity<FamilyDto> entity = new HttpEntity<>(family, headers);
    loginUser(ownerUsername);

    /* When */
    ResponseEntity<LinkedHashMap> responseEntity =
        restTemplate.exchange(
            createUrlWithPort("/admin/transferOwnership"),
            HttpMethod.PATCH,
            entity,
            LinkedHashMap.class);

    /* Then */
    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    // Get the family to test and make sure the owner was updated
    Map<String, Object> familyParams = new HashMap<>();
    familyParams.put("id", 3L);
    ResponseEntity<LinkedHashMap> familyResponse =
        restTemplate.exchange(
            createUrlWithPort("?id={id}"),
            HttpMethod.GET,
            entity,
            LinkedHashMap.class,
            familyParams);
    assertNotNull(familyResponse);
    assertNotNull(familyResponse.getBody());
    LinkedHashMap ownerObj = (LinkedHashMap) familyResponse.getBody().get("owner");
    assertEquals(
        4L, Integer.toUnsignedLong((Integer) ((LinkedHashMap) ownerObj.get("user")).get("id")));
  }

  @Test
  public void generateInvite_persistent_invite_success() {
    /* Given */
    MemberInviteDto invite =
        new MemberInviteDtoBuilder().withPersistence(true).withFamilyId(2L).build();
    HttpEntity<MemberInviteDto> entity = new HttpEntity<>(invite, headers);
    loginUser(ownerUsername);

    /* When */
    ResponseEntity<String> responseEntity =
        restTemplate.exchange(
            createUrlWithPort("/admin/invites/generate"), HttpMethod.POST, entity, String.class);

    /* Then */
    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    // get the family and make sure the invite code is populated
    Map<String, Object> familyParams = new HashMap<>();
    familyParams.put("id", 2L);
    ResponseEntity<LinkedHashMap> familyResponse =
        restTemplate.exchange(
            createUrlWithPort("?id={id}"),
            HttpMethod.GET,
            entity,
            LinkedHashMap.class,
            familyParams);
    assertNotNull(familyResponse);
    assertNotNull(familyResponse.getBody());
    assertNotNull(familyResponse.getBody().get("inviteCode"));
  }

  @Test
  public void generateInvite_one_time_use_invite_success() {
    /* Given */
    MemberInviteDto invite =
        new MemberInviteDtoBuilder()
            .withPersistence(false)
            .withRecipientEmail("testemail9@test.com")
            .withInitialRole(Role.ADMIN)
            .withFamilyId(2L)
            .build();
    HttpEntity<MemberInviteDto> entity = new HttpEntity<>(invite, headers);
    loginUser(ownerUsername);

    /* When */
    ResponseEntity<String> responseEntity =
        restTemplate.exchange(
            createUrlWithPort("/admin/invites/generate"), HttpMethod.POST, entity, String.class);

    /* Then */
    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    // get the list of invites for the family. if successful there should be an invite for the user
    // requested
    Map<String, Object> familyParams = new HashMap<>();
    familyParams.put("id", 2L);
    ResponseEntity<ArrayList> response =
        restTemplate.exchange(
            createUrlWithPort("/admin/invites?id={id}"),
            HttpMethod.GET,
            entity,
            ArrayList.class,
            familyParams);
    assertNotNull(response);
    assertNotNull(response.getBody());
    assertTrue(response.getBody().size() > 0);
    assertEquals(
        "testemail9@test.com", ((LinkedHashMap) response.getBody().get(0)).get("userEmail"));
    assertEquals(
        Role.ADMIN, Role.valueOf((String) ((LinkedHashMap) response.getBody().get(0)).get("role")));
    assertNotNull(((LinkedHashMap) response.getBody().get(0)).get("inviteCodeObj"));
  }

  @Test
  public void updateRoles_success() {
    /* Given */
    FamilyRoleUpdateRequest body = new FamilyRoleUpdateRequest();
    body.setFamilyId(1L);
    body.setMembers(
        Collections.singletonList(
            new FamilyMemberDtoBuilder()
                .withUser(new UserDtoBuilder().withId(3L).build())
                .withRole(Role.ADULT)
                .build()));
    HttpEntity<FamilyRoleUpdateRequest> entity = new HttpEntity<>(body, headers);
    loginUser(ownerUsername);

    /* When */
    ResponseEntity<String> responseEntity =
        restTemplate.exchange(
            createUrlWithPort("/admin/roles"), HttpMethod.POST, entity, String.class);

    /* Then */
    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    // get the family and examine the member data to make sure it was updated
    Map<String, Object> familyParams = new HashMap<>();
    familyParams.put("id", 1L);
    loginUser("testAdmin");
    ResponseEntity<LinkedHashMap> familyResponse =
        restTemplate.exchange(
            createUrlWithPort("?id={id}"),
            HttpMethod.GET,
            entity,
            LinkedHashMap.class,
            familyParams);
    assertNotNull(familyResponse);
    assertNotNull(familyResponse.getBody());
    LinkedHashMap memberData = (LinkedHashMap) familyResponse.getBody().get("memberData");
    assertEquals(Role.ADULT, Role.valueOf((String) memberData.get("role")));
  }

  private String createUrlWithPort(String endpoint) {
    return "http://localhost:" + port + "/api/v1/family" + endpoint;
  }

  private void loginUser(String username) {
    User user = new User();
    user.setUsername(username);
    user.setPassword(testPassword);
    HttpEntity<User> entity = new HttpEntity<>(user, headers);

    /* When */
    restTemplate.exchange(
        "http://localhost:" + port + "/api/services/auth/login",
        HttpMethod.POST,
        entity,
        LinkedHashMap.class);
  }
}
