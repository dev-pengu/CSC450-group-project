package com.familyorg.familyorganizationapp.service;

import com.familyorg.familyorganizationapp.domain.User;

public interface UserService {
	String createUser(User user);
	
	User getUserByUsername(String username);
	
	User getUserByEmail(String email);
	
	User getUserById(Long id);
}
