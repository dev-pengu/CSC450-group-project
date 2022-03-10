package com.familyorg.familyorganizationapp.DTO.builder;

import com.familyorg.familyorganizationapp.DTO.FamilyMemberDto;
import com.familyorg.familyorganizationapp.DTO.UserDto;
import com.familyorg.familyorganizationapp.domain.Role;

public class FamilyMemberDtoBuilder {

	private String eventColor;
	private UserDto user;
	private Long familyId;
	private Role role;

	public FamilyMemberDtoBuilder withUser(UserDto user) {
		this.user = user;
		return this;
	}

	public FamilyMemberDtoBuilder withFamilyId(Long familyId) {
		this.familyId = familyId;
		return this;
	}

	public FamilyMemberDtoBuilder withEventColor(String eventColor) {
		this.eventColor = eventColor;
		return this;
	}

	public FamilyMemberDtoBuilder withRole(Role role) {
		this.role = role;
		return this;
	}

	public FamilyMemberDto build() {
		return new FamilyMemberDto(
				this.user,
				this.eventColor,
				this.familyId,
				this.role);
	}
}
