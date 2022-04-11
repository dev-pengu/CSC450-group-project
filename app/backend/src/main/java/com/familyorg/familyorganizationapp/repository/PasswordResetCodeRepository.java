package com.familyorg.familyorganizationapp.repository;

import com.familyorg.familyorganizationapp.domain.PasswordResetCode;
import com.familyorg.familyorganizationapp.repository.custom.PasswordResetCodeRepositoryCustom;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface PasswordResetCodeRepository extends CrudRepository<PasswordResetCode, Long>,
  PasswordResetCodeRepositoryCustom {
  PasswordResetCode findByResetCode(@Param("resetCode") String resetCode);

}
