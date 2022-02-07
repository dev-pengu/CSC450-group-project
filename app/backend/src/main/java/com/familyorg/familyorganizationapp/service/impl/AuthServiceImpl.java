package com.familyorg.familyorganizationapp.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.familyorg.familyorganizationapp.Exception.ExistingUserException;
import com.familyorg.familyorganizationapp.Exception.UserNotFoundException;
import com.familyorg.familyorganizationapp.domain.User;
import com.familyorg.familyorganizationapp.repository.UserRepo;
import com.familyorg.familyorganizationapp.service.UserService;

@Service
public class AuthServiceImpl implements UserDetailsService, UserService {

	@Autowired
	UserRepo userRepo;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UserNotFoundException {
		User user = userRepo.findByUsername(username);
		if (user == null) {
			throw new UserNotFoundException("Username not found");
		}
		List<GrantedAuthority> auth = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");
		if (username.equals("admin")) {
			auth = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_ADMIN");
		}
		String password = user.getPassword();
		return new org.springframework.security.core.userdetails.User(username, password, auth);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String createUser(User user) {
		User existingUser = userRepo.findByUsername(user.getUsername());
		if (existingUser != null) {
			throw new ExistingUserException("Username already in use.");
		}
		existingUser = userRepo.findByEmail(user.getEmail());
		if (existingUser != null) {
			throw new ExistingUserException("Email already in use.");
		}
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		return userRepo.save(user).getId().toString();	
	}
}
