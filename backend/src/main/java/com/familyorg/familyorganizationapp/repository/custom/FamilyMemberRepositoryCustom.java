package com.familyorg.familyorganizationapp.repository.custom;

import java.util.List;
import com.familyorg.familyorganizationapp.DTO.ColorDto;

public interface FamilyMemberRepositoryCustom {
  List<ColorDto> getColorsByUser(Long userId);

}
