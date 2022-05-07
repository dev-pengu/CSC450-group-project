package com.familyorg.familyorganizationapp.repository.impl;

import java.util.List;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import com.familyorg.familyorganizationapp.domain.MemberInvite;
import com.familyorg.familyorganizationapp.domain.QMemberInvite;
import com.familyorg.familyorganizationapp.repository.custom.MemberInviteRepositoryCustom;

public class MemberInviteRepositoryImpl extends QuerydslRepositorySupport
    implements MemberInviteRepositoryCustom {
  public MemberInviteRepositoryImpl() {
    super(MemberInvite.class);
  }

  private QMemberInvite inviteTable = QMemberInvite.memberInvite;

  @Override
  public List<MemberInvite> getByFamilyId(Long familyId) {
    List<MemberInvite> invites =
        from(inviteTable).where(inviteTable.family.id.eq(familyId)).fetch();
    return invites;
  }
}
