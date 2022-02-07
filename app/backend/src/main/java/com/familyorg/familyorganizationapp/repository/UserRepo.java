package com.familyorg.familyorganizationapp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.familyorg.familyorganizationapp.domain.User;


public interface UserRepo extends CrudRepository<User, Long> {
	User findByUsername(@Param("username") String username);
	User findByEmail(@Param("email") String email);
}
