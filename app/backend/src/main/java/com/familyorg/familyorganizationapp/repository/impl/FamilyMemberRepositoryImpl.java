package com.familyorg.familyorganizationapp.repository.impl;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.familyorg.familyorganizationapp.domain.FamilyMembers;
import com.familyorg.familyorganizationapp.repository.custom.FamilyMemberRepositoryCustom;

@Repository
public class FamilyMemberRepositoryImpl extends QuerydslRepositorySupport
	implements FamilyMemberRepositoryCustom {
	public FamilyMemberRepositoryImpl() {
		super(FamilyMembers.class);
	}
}
