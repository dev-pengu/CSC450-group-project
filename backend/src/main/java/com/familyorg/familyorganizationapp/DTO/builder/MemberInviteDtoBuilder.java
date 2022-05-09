package com.familyorg.familyorganizationapp.DTO.builder;

import com.familyorg.familyorganizationapp.DTO.MemberInviteDto;
import com.familyorg.familyorganizationapp.domain.Role;

public class MemberInviteDtoBuilder implements DtoBuilder<MemberInviteDto> {

	private Long familyId;
	private String recipientEmail;
	private Role initialRole;
	private boolean persistent;

	public MemberInviteDtoBuilder withFamilyId(Long id) {
		this.familyId = id;
		return this;
	}

	public MemberInviteDtoBuilder withRecipientEmail(String email) {
		this.recipientEmail = email;
		return this;
	}

	public MemberInviteDtoBuilder withInitialRole(Role role) {
		this.initialRole = role;
		return this;
	}

	public MemberInviteDtoBuilder withPersistence(boolean persistence) {
		this.persistent = persistence;
		return this;
	}

	@Override
	public MemberInviteDto build() {
		return new MemberInviteDto(familyId, recipientEmail, initialRole, persistent);
	}

}
