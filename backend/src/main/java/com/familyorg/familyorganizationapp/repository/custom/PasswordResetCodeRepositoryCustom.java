package com.familyorg.familyorganizationapp.repository.custom;

import com.familyorg.familyorganizationapp.domain.PasswordResetCode;
import java.util.List;

public interface PasswordResetCodeRepositoryCustom {
  List<PasswordResetCode> getResetCodesByUser(Long userId);

  void batchExpire(List<Long> codeIds);
}
