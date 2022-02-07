package com.familyorg.familyorganizationapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.familyorg.familyorganizationapp.Exception.ExistingUserException;
import com.familyorg.familyorganizationapp.domain.User;
import com.familyorg.familyorganizationapp.service.UserService;

@RestController
@RequestMapping("/api/services/auth")
public class AuthController {
	@Autowired
	UserService userService;
	
	private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);
	
	/* Login endpoint is automatically included with spring security
	 * use endpoint /api/services/auth/login and send "username" and "password" in body
	 */
	
	
	/**
	 * Create a new user. If username or email is already in use, will
	 * return HttpStatus 409 and indication of which field is already in use.
	 * @param user
	 * @return Http Response body and status code
	 */
	@PostMapping()
	public ResponseEntity<String> saveUser(@RequestBody User user) {
		try {
			return new ResponseEntity<>(userService.createUser(user),
				HttpStatus.CREATED);
		} catch (ExistingUserException e) { 
			return new ResponseEntity<>(e.getMessage(),
					HttpStatus.CONFLICT);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), 
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
