package com.familyorg.familyorganizationapp.DTO;

import java.util.Objects;

import com.familyorg.familyorganizationapp.domain.FamilyMembers;
import com.familyorg.familyorganizationapp.domain.Role;

public class FamilyMemberDto {
	private String eventColor;
	private UserDto user;
	private Long familyId;
	private Role role;
	
	public FamilyMemberDto(UserDto user, String eventColor, Long familyId, Role role) {
		super();
		this.user = user;
		this.eventColor = eventColor;
		this.familyId = familyId;
		this.role = role;
	}

	public String getEventColor() {
		return eventColor;
	}

	public String getUsername() {
		return user.getUsername();
	}

	public String getEmail() {
		return user.getEmail();
	}

	public Long getFamilyId() {
		return familyId;
	}
	
	public UserDto getUser() {
		return user;
	}

	public Role getRole() {
		return role;
	}

	@Override
	public String toString() {
		return "FamilyMemberDto [eventColor=" + eventColor + ", user=" + user
				+ ", familyId=" + familyId + ", role=" + role + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(user, eventColor, familyId, role);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FamilyMemberDto other = (FamilyMemberDto) obj;
		return Objects.equals(user, other.user) && Objects.equals(eventColor, other.eventColor)
				&& Objects.equals(familyId, other.familyId) && Objects.equals(role, other.role);
	}
	
	public static FamilyMemberDto fromFamilyMemberObj(FamilyMembers familyMember) {
		return new FamilyMemberDto(
				UserDto.fromUserObj(familyMember.getUser()),
				familyMember.getEventColor(),
				familyMember.getFamily().getId(),
				familyMember.getRole());
	}
	
}
