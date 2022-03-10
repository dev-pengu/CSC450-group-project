package com.familyorg.familyorganizationapp.repository.impl;

import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.familyorg.familyorganizationapp.domain.Family;
import com.familyorg.familyorganizationapp.domain.QFamily;
import com.familyorg.familyorganizationapp.domain.QFamilyMembers;
import com.familyorg.familyorganizationapp.repository.custom.FamilyRepositoryCustom;

@Repository
public class FamilyRepositoryImpl extends QuerydslRepositorySupport
	implements FamilyRepositoryCustom {

	private QFamily familyTable = QFamily.family;
	private QFamilyMembers familyMembersTable = QFamilyMembers.familyMembers;

	public FamilyRepositoryImpl() {
		super(Family.class);
	}

	@Override
	public List<Family> getFamiliesByUserId(Long userId) {
		List<Family> families =
				from(familyTable)
				.innerJoin(familyTable.members, familyMembersTable)
				.where(familyMembersTable.user.id.eq(userId))
				.fetch();
		return families;
	}

}
