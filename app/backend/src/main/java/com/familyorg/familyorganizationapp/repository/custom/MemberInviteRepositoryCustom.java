package com.familyorg.familyorganizationapp.repository.custom;

import java.util.List;
import com.familyorg.familyorganizationapp.domain.MemberInvite;

public interface MemberInviteRepositoryCustom {
  List<MemberInvite> getByFamilyId(Long familyId);
}
