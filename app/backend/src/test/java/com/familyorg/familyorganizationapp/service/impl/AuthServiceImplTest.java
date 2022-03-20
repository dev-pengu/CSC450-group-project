package com.familyorg.familyorganizationapp.service.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.familyorg.familyorganizationapp.service.AuthService;

public class AuthServiceImplTest {
  private AuthService authService;

  @BeforeEach
  public void setup() {
    authService = new AuthServiceImpl();
  }

  @Test
  public void test_password_requirements() {
    String password = "aB37neq!H9f$";

    boolean meetsRequirements = authService.verifyPasswordRequirements(password);

    assertTrue(meetsRequirements);
  }

  @Test
  public void when_short_password_then_password_doesnt_meet_requirements() {
    String password = "aB37neq!H9";

    boolean meetsRequirements = authService.verifyPasswordRequirements(password);

    assertFalse(meetsRequirements);
  }

  @Test
  public void when_missing_uppercase_then_password_doesnt_meet_requirements() {
    String password = "ab37neq!h9f$";

    boolean meetsRequirements = authService.verifyPasswordRequirements(password);

    assertFalse(meetsRequirements);
  }

  @Test
  public void when_missing_lowercase_then_password_doesnt_meet_requirements() {
    String password = "AB37NEQ!H9F$";

    boolean meetsRequirements = authService.verifyPasswordRequirements(password);

    assertFalse(meetsRequirements);
  }

  @Test
  public void when_missing_number_then_password_doesnt_meet_requirements() {
    String password = "passWord$!lo";

    boolean meetsRequirements = authService.verifyPasswordRequirements(password);

    assertFalse(meetsRequirements);
  }

  @Test
  public void when_missing_special_then_password_doesnt_meet_requirements() {
    String password = "aB37neq1H9fg";

    boolean meetsRequirements = authService.verifyPasswordRequirements(password);

    assertFalse(meetsRequirements);
  }
}
