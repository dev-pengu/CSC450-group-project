package com.familyorg.familyorganizationapp.controller;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/utility")
public class UtilityController {
  Logger logger = LoggerFactory.getLogger(UtilityController.class);

  @GetMapping("/timezones")
  public ResponseEntity<List<String>> getAvailableTimezones() {
    Set<String> timezones = ZoneId.getAvailableZoneIds().stream()
        .filter(zone -> !zone.contains("System") && zone.contains("/")).collect(Collectors.toSet());
    List<String> sortedTimezones = new ArrayList<String>(timezones);
    Collections.sort(sortedTimezones);
    return new ResponseEntity<>(sortedTimezones, HttpStatus.OK);
  }
}
