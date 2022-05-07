package com.familyorg.familyorganizationapp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import com.familyorg.familyorganizationapp.domain.User;
import com.familyorg.familyorganizationapp.repository.custom.UserRepositoryCustom;


public interface UserRepository extends CrudRepository<User, Long>, UserRepositoryCustom {
  User findByUsername(@Param("username") String username);

  User findByEmail(@Param("email") String email);
}
