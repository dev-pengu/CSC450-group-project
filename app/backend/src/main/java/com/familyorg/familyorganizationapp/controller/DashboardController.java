package com.familyorg.familyorganizationapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/services/dashboard")
public class DashboardController {
	
	private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

	
	@GetMapping()
	@Secured("ROLE_USER")
	public ResponseEntity<String> getDashboardData() {
		return new ResponseEntity<>("Hello", HttpStatus.OK);
	}
}
