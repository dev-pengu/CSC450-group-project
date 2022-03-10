package com.familyorg.familyorganizationapp.repository;

import com.familyorg.familyorganizationapp.domain.MemberInvite;
import com.familyorg.familyorganizationapp.domain.id.MemberInviteId;
import com.familyorg.familyorganizationapp.repository.custom.MemberInviteRepositoryCustom;
import org.springframework.data.repository.CrudRepository;

public interface MemberInviteRepository extends CrudRepository<MemberInvite, MemberInviteId>,
    MemberInviteRepositoryCustom {
  // Spring data methods only
}
