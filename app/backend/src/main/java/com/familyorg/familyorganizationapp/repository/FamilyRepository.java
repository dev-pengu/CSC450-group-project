package com.familyorg.familyorganizationapp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.familyorg.familyorganizationapp.domain.Family;
import com.familyorg.familyorganizationapp.repository.custom.FamilyRepositoryCustom;

public interface FamilyRepository extends CrudRepository<Family, Long>, FamilyRepositoryCustom {
	// Spring data methods only
	Family findByInviteCode(@Param("inviteCode") String inviteCode);
}
