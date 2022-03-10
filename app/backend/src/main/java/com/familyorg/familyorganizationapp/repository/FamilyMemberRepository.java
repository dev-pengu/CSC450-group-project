package com.familyorg.familyorganizationapp.repository;

import org.springframework.data.repository.CrudRepository;

import com.familyorg.familyorganizationapp.domain.FamilyMemberId;
import com.familyorg.familyorganizationapp.domain.FamilyMembers;
import com.familyorg.familyorganizationapp.repository.custom.FamilyMemberRepositoryCustom;

public interface FamilyMemberRepository extends CrudRepository<FamilyMembers, FamilyMemberId>,
	FamilyMemberRepositoryCustom {
	// Spring data methods only
}
