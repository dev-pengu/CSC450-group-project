package com.familyorg.familyorganizationapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.familyorg.familyorganizationapp.Exception.ExistingUserException;
import com.familyorg.familyorganizationapp.Exception.IncorrectCredentialsException;
import com.familyorg.familyorganizationapp.domain.User;
import com.familyorg.familyorganizationapp.service.SecurityService;
import com.familyorg.familyorganizationapp.service.UserService;

@RestController
@RequestMapping("/api/services/auth")
public class AuthController {
	@Autowired
	private UserService userService;
	@Autowired
	private SecurityService securityService;

	private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);


	/**
	 * Create a new user. If username or email is already in use, will
	 * return HttpStatus 409 and indication of which field is already in use.
	 * @param user
	 * @return Http Response body and status code
	 */
	@PostMapping()
	public ResponseEntity<String> saveUser(@RequestBody User user) {
		try {
			String originalPassword = user.getPassword();
			if (userService.createUser(user) != null) {
				securityService.autologin(user.getUsername(), originalPassword);
				return new ResponseEntity<>("User created successfully!",
						HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>("Error when creating user.",
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (ExistingUserException e) {
			return new ResponseEntity<>(e.getMessage(),
					HttpStatus.CONFLICT);
		} catch (Exception e) {
			LOG.error("Error", e);
			return new ResponseEntity<>(e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody User user) {
		try {
			securityService.autologin(user.getUsername(), user.getPassword());
			return new ResponseEntity<>("Successfully logged in", HttpStatus.OK);
		} catch (IncorrectCredentialsException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
