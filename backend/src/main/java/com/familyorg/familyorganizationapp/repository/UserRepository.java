package com.familyorg.familyorganizationapp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import com.familyorg.familyorganizationapp.domain.User;
import com.familyorg.familyorganizationapp.repository.custom.UserRepositoryCustom;


public interface UserRepository extends CrudRepository<User, Long>, UserRepositoryCustom {
  User findByUsernameIgnoreCase(@Param("username") String username);

  User findByEmailIgnoreCase(@Param("email") String email);
}
