package com.familyorg.familyorganizationapp.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.familyorg.familyorganizationapp.DTO.UserDto;
import com.familyorg.familyorganizationapp.DTO.builder.UserDtoBuilder;
import com.familyorg.familyorganizationapp.Exception.ApiExceptionCode;
import com.familyorg.familyorganizationapp.FamilyOrganizationAppApplication;
import com.familyorg.familyorganizationapp.domain.User;
import com.familyorg.familyorganizationapp.service.MessagingService;
import com.familyorg.familyorganizationapp.utility.HibernateUtil;
import java.io.IOException;
import java.util.LinkedHashMap;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.web.Link;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.DefaultResponseErrorHandler;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    classes = FamilyOrganizationAppApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class AuthControllerIntTest {

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
  public void checkUsername_and_user_doesnt_exist() throws Exception {
    /* Given */
    String username = "testuser2";
    HttpEntity entity = new HttpEntity(null, headers);

    /* When */
    ResponseEntity<Boolean> responseEntity =
        restTemplate.exchange(
            createUrlWithPort("/usernameCheck?username=" + username),
            HttpMethod.GET,
            entity,
            Boolean.class);

    /* Then */
    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertTrue(responseEntity.getBody());
  }

  @Test
  public void checkEmail_and_user_doesnt_exist() throws Exception {
    /* Given */
    String email = "testuser2@test.com";
    HttpEntity entity = new HttpEntity(null, headers);

    /* When */
    ResponseEntity<Boolean> responseEntity =
        restTemplate.exchange(
            createUrlWithPort("/emailCheck?email=" + email), HttpMethod.GET, entity, Boolean.class);

    /* Then */
    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertTrue(responseEntity.getBody());
  }

  @Test
  public void checkUsername_and_user_does_exist() throws Exception {
    /* Given */
    String username = "testuser";
    HttpEntity entity = new HttpEntity(null, headers);

    /* When */
    ResponseEntity<Boolean> responseEntity =
        restTemplate.exchange(
            createUrlWithPort("/usernameCheck?username=" + username),
            HttpMethod.GET,
            entity,
            Boolean.class);

    /* Then */
    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertFalse(responseEntity.getBody());
  }

  @Test
  public void checkEmail_and_user_does_exist() throws Exception {
    /* Given */
    String email = "testuser@test.com";
    HttpEntity entity = new HttpEntity(null, headers);

    /* When */
    ResponseEntity<Boolean> responseEntity =
        restTemplate.exchange(
            createUrlWithPort("/emailCheck?email=" + email), HttpMethod.GET, entity, Boolean.class);

    /* Then */
    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertFalse(responseEntity.getBody());
  }

  @Test
  public void registerUser_success() throws Exception {
    /* Given */
    User user = new User();
    user.setUsername("testuser3");
    user.setEmail("testuser3@test.com");
    user.setPassword(testPassword);
    user.setTimezone("US/Central");
    user.setFirstName("Test");
    user.setLastName("User3");
    HttpEntity<User> entity = new HttpEntity<>(user, headers);

    /* When */
    ResponseEntity<LinkedHashMap> responseEntity =
        restTemplate.exchange(
            createUrlWithPort("/register"), HttpMethod.POST, entity, LinkedHashMap.class);

    /* Then */
    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertEquals(user.getUsername(), responseEntity.getBody().get("username"));
    assertEquals(user.getFirstName(), responseEntity.getBody().get("firstName"));
    assertEquals(user.getLastName(), responseEntity.getBody().get("lastName"));
    assertEquals(user.getEmail(), responseEntity.getBody().get("email"));
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
  }

  @Test
  public void registerUser_user_exists_with_username() {
    /* Given */
    User user = new User();
    user.setUsername(ownerUsername);
    user.setEmail("testuser3@test.com");
    user.setPassword(testPassword);
    user.setTimezone("US/Central");
    user.setFirstName("Test");
    user.setLastName("User");
    HttpEntity<User> entity = new HttpEntity<>(user, headers);

    /* When */
    ResponseEntity<LinkedHashMap> responseEntity =
        restTemplate.exchange(
            createUrlWithPort("/register"), HttpMethod.POST, entity, LinkedHashMap.class);

    /* Then */
    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertEquals(
        ApiExceptionCode.USERNAME_IN_USE.getCode(), responseEntity.getBody().get("errorCode"));
    assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
  }

  @Test
  public void registerUser_user_exists_with_email() {
    /* Given */
    User user = new User();
    user.setUsername("testuser4");
    user.setEmail(ownerUsername + "@" + testDomain);
    user.setPassword(testPassword);
    user.setTimezone("US/Central");
    user.setFirstName("Test");
    user.setLastName("User");
    HttpEntity<User> entity = new HttpEntity<>(user, headers);

    /* When */
    ResponseEntity<LinkedHashMap> responseEntity =
        restTemplate.exchange(
            createUrlWithPort("/register"), HttpMethod.POST, entity, LinkedHashMap.class);

    /* Then */
    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertEquals(
        ApiExceptionCode.EMAIL_IN_USE.getCode(), responseEntity.getBody().get("errorCode"));
    assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
  }

  @Test
  public void login_with_username_success() {
    /* Given */
    User user = new User();
    user.setUsername(ownerUsername);
    user.setPassword(testPassword);
    HttpEntity<User> entity = new HttpEntity<>(user, headers);

    /* When */
    ResponseEntity<LinkedHashMap> responseEntity =
        restTemplate.exchange(
            createUrlWithPort("/login"), HttpMethod.POST, entity, LinkedHashMap.class);

    /* Then */
    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertEquals(ownerUsername, responseEntity.getBody().get("username"));
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test
  public void login_with_email_success() {
    /* Given */
    User user = new User();
    user.setEmail(ownerUsername + "@" + testDomain);
    user.setPassword(testPassword);
    HttpEntity<User> entity = new HttpEntity<>(user, headers);

    /* When */
    ResponseEntity<LinkedHashMap> responseEntity =
        restTemplate.exchange(
            createUrlWithPort("/login"), HttpMethod.POST, entity, LinkedHashMap.class);

    /* Then */
    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertEquals(ownerUsername, responseEntity.getBody().get("username"));
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test
  public void login_with_bad_credentials() {
    /* Given */
    User user = new User();
    user.setEmail(ownerUsername + "@" + testDomain);
    user.setPassword("thisisthewrongpassword");
    HttpEntity<User> entity = new HttpEntity<>(user, headers);

    /* When */
    ResponseEntity<LinkedHashMap> responseEntity =
        restTemplate.exchange(
            createUrlWithPort("/login"), HttpMethod.POST, entity, LinkedHashMap.class);

    /* Then */
    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
  }

  @Test
  public void lockout_after_three_failed_login_attempts() throws InterruptedException {
    /* Given */
    User user = new User();
    user.setEmail(ownerUsername + "@" + testDomain);
    user.setPassword("thisisthewrongpassword");
    HttpEntity<User> entity = new HttpEntity<>(user, headers);

    /* When */
    for (int i = 0; i < 4; i++) {
      ResponseEntity<LinkedHashMap> responseEntity =
          restTemplate.exchange(
              createUrlWithPort("/login"), HttpMethod.POST, entity, LinkedHashMap.class);
      assertNotNull(responseEntity);
      assertNotNull(responseEntity.getBody());
      if (i < 3) {
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        Thread.sleep(1000);
      } else {
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals(
            ApiExceptionCode.ACCOUNT_LOCKED.getCode(), responseEntity.getBody().get("errorCode"));
      }
    }
  }

  @Test
  public void test_change_password_with_old_password_success() {
    /* Given */
    String newPassword = "$ph43VZ3#5&#cxuc";
    UserDto user =
        new UserDtoBuilder()
            .setNewPassword(newPassword)
            .setOldPassword(testPassword)
            .withUsername(adultUsername)
            .build();
    HttpEntity<UserDto> entity = new HttpEntity<>(user, headers);
    loginUser(adultUsername);

    /* When */
    ResponseEntity<String> responseEntity =
        restTemplate.exchange(
            createUrlWithPort("/changePassword"), HttpMethod.POST, entity, String.class);

    /* Then */
    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test
  public void test_change_password_doesnt_meet_requirements() {
    /* Given */
    String newPassword = "password";
    UserDto user =
        new UserDtoBuilder()
            .setNewPassword(newPassword)
            .setOldPassword(testPassword)
            .withUsername(ownerUsername)
            .build();
    HttpEntity<UserDto> entity = new HttpEntity<>(user, headers);
    loginUser(ownerUsername);

    /* When */
    ResponseEntity<LinkedHashMap> responseEntity =
        restTemplate.exchange(
            createUrlWithPort("/changePassword"), HttpMethod.POST, entity, LinkedHashMap.class);

    /* Then */
    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(
        ApiExceptionCode.PASSWORD_MINIMUM_REQUIREMENTS_NOT_MET.getCode(),
        responseEntity.getBody().get("errorCode"));
  }

  @Test
  public void test_change_password_old_password_incorrect() {
    /* Given */
    String newPassword = "$ph43VZ3#5&#cxuc";
    UserDto user =
        new UserDtoBuilder()
            .setNewPassword(newPassword)
            .setOldPassword("thisisthewrongpassword")
            .withUsername(adminUsername)
            .build();
    HttpEntity<UserDto> entity = new HttpEntity<>(user, headers);
    loginUser(adminUsername);

    /* When */
    ResponseEntity<LinkedHashMap> responseEntity =
        restTemplate.exchange(
            createUrlWithPort("/changePassword"), HttpMethod.POST, entity, LinkedHashMap.class);

    /* Then */
    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
  }

  @Test
  public void test_change_password_with_reset_code() {
    /* Given */
    String newPassword = "$ph43VZ3#5&#cxuc";
    String resetCode = "8f82148c-612c-40d5-af56-fd353a9c08a8";
    UserDto user =
        new UserDtoBuilder()
            .setRestCode(resetCode)
            .setNewPassword(newPassword)
            .withUsername(adminUsername)
            .build();
    HttpEntity<UserDto> entity = new HttpEntity<>(user, headers);
    loginUser(adminUsername);

    /* When */
    ResponseEntity<String> responseEntity =
        restTemplate.exchange(
            createUrlWithPort("/changePassword"), HttpMethod.POST, entity, String.class);

    /* Then */
    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test
  public void test_change_password_expired_reset_code() {
    /* Given */
    String newPassword = "$ph43VZ3#5&#cxuc";
    String resetCode = "5794be52-c089-444b-b84c-7691299986b0";
    UserDto user =
        new UserDtoBuilder()
            .setRestCode(resetCode)
            .setNewPassword(testPassword)
            .withUsername(ownerUsername)
            .build();
    HttpEntity<UserDto> entity = new HttpEntity<>(user, headers);
    loginUser(adminUsername);

    /* When */
    ResponseEntity<LinkedHashMap> responseEntity =
        restTemplate.exchange(
            createUrlWithPort("/changePassword"), HttpMethod.POST, entity, LinkedHashMap.class);

    /* Then */
    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
    assertEquals(
        ApiExceptionCode.ILLEGAL_ACTION_REQUESTED.getCode(),
        responseEntity.getBody().get("errorCode"));
  }

  private String createUrlWithPort(String endpoint) {
    return "http://localhost:" + port + "/api/services/auth" + endpoint;
  }

  private void loginUser(String username) {
    User user = new User();
    user.setUsername(username);
    user.setPassword(testPassword);
    HttpEntity<User> entity = new HttpEntity<>(user, headers);

    /* When */
    ResponseEntity<LinkedHashMap> responseEntity =
        restTemplate.exchange(
            createUrlWithPort("/login"), HttpMethod.POST, entity, LinkedHashMap.class);
  }
}
