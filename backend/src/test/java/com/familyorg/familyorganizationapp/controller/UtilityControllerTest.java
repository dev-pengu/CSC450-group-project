package com.familyorg.familyorganizationapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

public class UtilityControllerTest {
  private UtilityController utilityController;

  @BeforeEach
  public void setup() {
    utilityController = new UtilityController();
  }

  @Test
  public void getAvailableTimezones_success() {
    /* Given */

    /* When */
    ResponseEntity<List<String>> response = utilityController.getAvailableTimezones();

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertFalse(response.getBody().isEmpty());
  }
}
