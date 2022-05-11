package com.familyorg.familyorganizationapp.repository.custom;

import java.util.List;
import com.familyorg.familyorganizationapp.domain.Family;

public interface FamilyRepositoryCustom {
  List<Family> getFamiliesByUserId(Long userId);

  List<Long> getFamilyIdsByUser(String username);

  List<Family> getFamiliesByUser(String username);
}
