package com.familyorg.familyorganizationapp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import com.familyorg.familyorganizationapp.domain.MemberInvite;
import com.familyorg.familyorganizationapp.domain.id.MemberInviteId;
import com.familyorg.familyorganizationapp.repository.custom.MemberInviteRepositoryCustom;

public interface MemberInviteRepository
    extends CrudRepository<MemberInvite, MemberInviteId>, MemberInviteRepositoryCustom {
  // Spring data methods only
  MemberInvite findByInviteCode(@Param("inviteCode") String inviteCode);
}
