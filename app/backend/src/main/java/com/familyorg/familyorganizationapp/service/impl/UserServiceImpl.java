package com.familyorg.familyorganizationapp.service.impl;

import com.familyorg.familyorganizationapp.Exception.ExistingUserException;
import com.familyorg.familyorganizationapp.domain.User;
import com.familyorg.familyorganizationapp.repository.UserRepository;
import com.familyorg.familyorganizationapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  UserRepository userRepository;

  @Autowired
  BCryptPasswordEncoder bCryptPasswordEncoder;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public String createUser(User user) {
    User existingUser = userRepository.findByUsername(user.getUsername());
    if (existingUser != null) {
      throw new ExistingUserException("Username already in use.");
    }
    existingUser = userRepository.findByEmail(user.getEmail());
    if (existingUser != null) {
      throw new ExistingUserException("Email already in use.");
    }
    user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
    return userRepository.save(user).getId().toString();
  }

  @Override
  public User getUserByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  @Override
  public User getUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  @Override
  public User getUserById(Long id) {
    return userRepository.findById(id).get();
  }
}
