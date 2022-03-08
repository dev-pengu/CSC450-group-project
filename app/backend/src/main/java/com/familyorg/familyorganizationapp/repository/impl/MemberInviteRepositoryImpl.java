package com.familyorg.familyorganizationapp.repository.impl;

import com.familyorg.familyorganizationapp.domain.MemberInvite;
import com.familyorg.familyorganizationapp.repository.custom.MemberInviteRepositoryCustom;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class MemberInviteRepositoryImpl extends QuerydslRepositorySupport
    implements MemberInviteRepositoryCustom {
  public MemberInviteRepositoryImpl() { super(MemberInvite.class); }
}
